package LLD.RateLimiterApp.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public class SlidingWindowLogRateLimitStrategy implements RateLimitStrategy {
    private final Map<String, AtomicReference<RequestLog>> requestLogs = new ConcurrentHashMap<>();

    @Override
    public RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs) {
        AtomicReference<RequestLog> requestLogRef = requestLogs.computeIfAbsent(getKey(rule),
                key -> new AtomicReference<>(new RequestLog(new ArrayDeque<>())));

        while (true) {
            RequestLog current = requestLogRef.get();
            Deque<Long> activeTimestamps = evictExpiredRequests(current.timestamps, currentTimeMs,
                    rule.getWindowSizeMs());

            if (activeTimestamps.size() >= rule.getLimit()) {
                long oldestRequestTime = activeTimestamps.peekFirst();
                long retryAfterMs = rule.getWindowSizeMs() - (currentTimeMs - oldestRequestTime);
                return new RateLimitResult(false, 0, Math.max(1, retryAfterMs),
                        "Sliding window log limit exceeded");
            }

            activeTimestamps.addLast(currentTimeMs);
            RequestLog next = new RequestLog(activeTimestamps);
            if (requestLogRef.compareAndSet(current, next)) {
                int remaining = rule.getLimit() - next.timestamps.size();
                return new RateLimitResult(true, remaining, 0,
                        "Request allowed by sliding window log");
            }
        }
    }

    @Override
    public void rollback(RateLimitRuleEntity rule, long currentTimeMs) {
        AtomicReference<RequestLog> requestLogRef = requestLogs.get(getKey(rule));
        if (requestLogRef == null) {
            return;
        }

        while (true) {
            RequestLog current = requestLogRef.get();
            Deque<Long> timestamps = new ArrayDeque<>(current.timestamps);
            if (timestamps.isEmpty()) {
                return;
            }
            timestamps.removeLast();
            if (requestLogRef.compareAndSet(current, new RequestLog(timestamps))) {
                return;
            }
        }
    }

    private Deque<Long> evictExpiredRequests(Deque<Long> timestamps, long currentTimeMs,
            long windowSizeMs) {
        Deque<Long> activeTimestamps = new ArrayDeque<>(timestamps);
        while (!activeTimestamps.isEmpty()
                && currentTimeMs - activeTimestamps.peekFirst() >= windowSizeMs) {
            activeTimestamps.removeFirst();
        }
        return activeTimestamps;
    }

    private String getKey(RateLimitRuleEntity rule) {
        return rule.getStateKey();
    }

    private static class RequestLog {
        private final Deque<Long> timestamps;

        private RequestLog(Deque<Long> timestamps) {
            this.timestamps = timestamps;
        }
    }
}
