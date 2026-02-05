package LLD;
/*
Problem Statement:

Design a system that can apply rate limiting to APIs exposed by a backend service. The system should support multiple rate limiting strategies:

Fixed Window
Sliding Window Log
Sliding Window Counter
Token Bucket
Leaky Bucket
*/

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

interface RateLimitStrategy {
    public boolean isReqAllowed(String token, String resourcePath);
}

class TokenBucketLimiter implements RateLimitStrategy {

    class Bucket {
        final long ts, tokens;

        public Bucket(long tokens, long ts) {
            this.tokens = tokens;
            this.ts = ts;
        }
    }

    final ConcurrentHashMap<String, AtomicReference<Bucket>> buckets = new ConcurrentHashMap<>();

    final long capacity, tokensPerSec;

    public TokenBucketLimiter(long capacity, long tokensPerSec) {
        this.capacity = capacity;
        this.tokensPerSec = tokensPerSec;
    }

    @Override
    public boolean isReqAllowed(String token, String resourcePath) {
        AtomicReference<Bucket> bucketRef = buckets.computeIfAbsent(token,
                k -> new AtomicReference<>(new Bucket(capacity, System.currentTimeMillis())));

        while (true) {
            Bucket old = bucketRef.get();
            long now = System.currentTimeMillis();
            long refillAmt = (now - old.ts) * tokensPerSec;
            long newTokens = Math.min(capacity, old.tokens + refillAmt);

            if (newTokens < 1) {
                return false;
            }

            long nextRefilTime = (refillAmt > 0) ? now : old.ts;
            Bucket newBucket = new Bucket(newTokens - 1, nextRefilTime);
            if (bucketRef.compareAndSet(old, newBucket)) {
                return true;
            }
        }
    }

}

class FixedWindowLimiter implements RateLimitStrategy {

    class HighWindow {
        final LongAdder count = new LongAdder();
        final long timeMs;

        public HighWindow(long time) {
            this.timeMs = time;
        }
    }

    class Window {
        AtomicInteger reqsCount = new AtomicInteger(0);
        final long timeMs;

        public Window(long timeMs) {
            this.timeMs = timeMs;
        }

        public long getTimeMs() {
            return timeMs;
        }

        public int increaseAndGet() {
            return reqsCount.incrementAndGet();
        }
    }

    final int limit;
    final long windowSizeMs;
    final Map<String, HighWindow> windowMap;

    public FixedWindowLimiter(int limit, long windowSizeMs) {
        this.limit = limit;
        this.windowSizeMs = windowSizeMs;
        this.windowMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isReqAllowed(String token, String resourcePath) {
        long currTime = System.currentTimeMillis();
        HighWindow window = windowMap.compute(token, (key, existing) -> {
            if (existing == null || currTime - existing.timeMs >= this.windowSizeMs) {
                return new HighWindow(currTime);
            }
            return existing;
        });

        window.count.increment();
        return window.count.sum() <= this.limit;
    }
}

public class RateLimiterApp {

    RateLimitStrategy rateLimiter;

    public RateLimiterApp(RateLimitStrategy rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    static RateLimitStrategy getRateLimiter() {
        // return new TokenBucketLimiter(8, 1);
        return new FixedWindowLimiter(5, 60_000);
    }

    public static void main(String a[]) {
        RateLimiterApp app = new RateLimiterApp(getRateLimiter());
        ExecutorService executors = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(10);
        int count = 10;
        while (count-- > 0) {
            final int temp = count;
            executors.submit(() -> {
                try {
                    if (app.isReqAllowed("JWT_A", "RES_A")) {
                        System.out.println("allowed" + temp);
                    } else {
                        System.out.println("not allowed" + temp);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
            executors.shutdown();
        } catch (Exception e) {
        }
    }

    public boolean isReqAllowed(String token, String resourcePath) {
        return rateLimiter.isReqAllowed(token, resourcePath);
    }
}
