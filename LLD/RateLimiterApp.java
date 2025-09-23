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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

interface IRateLimiter {
    public boolean isReqAllowed(String token, String resourcePath);
}

class FixedWindowLimiter implements IRateLimiter {

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
    final ConcurrentHashMap<String, Window> windowMap;
    final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public FixedWindowLimiter(int limit, long windowSizeMs) {
        this.limit = limit;
        this.windowSizeMs = windowSizeMs;
        this.windowMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isReqAllowed(String token, String resourcePath) {
        String key = token + resourcePath;
        locks.putIfAbsent(key, new Object());
        synchronized (locks.get(key)) {
            Window window = windowMap.get(token);
            if (window == null || System.currentTimeMillis() - window.getTimeMs() >= this.windowSizeMs) {
                window = new Window(System.currentTimeMillis());
                windowMap.put(token, window);
            }
            if (window.increaseAndGet() <= this.limit) {
                return true;
            }
            return false;
        }
    }
}

public class RateLimiterApp {

    IRateLimiter rateLimiter;

    public RateLimiterApp(IRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public static void main(String a[]) {
        RateLimiterApp app = new RateLimiterApp(new FixedWindowLimiter(5, 60_000));
        int count = 10;
        while (count-- > 0) {
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (app.isReqAllowed("JWT_A", "RES_A")) {

                    }
                }
            });
            t1.start();
        }
    }

    public boolean isReqAllowed(String token, String resourcePath) {
        return rateLimiter.isReqAllowed(token, resourcePath);
    }
}
