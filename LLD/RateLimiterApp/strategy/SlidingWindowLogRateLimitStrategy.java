package LLD.RateLimiterApp.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public class SlidingWindowLogRateLimitStrategy implements RateLimitStrategy {
    private final Map<String, Deque<Long>> requestLogs = new ConcurrentHashMap<>();

    @Override
    public RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs) {
        Deque<Long> timestamps = requestLogs.computeIfAbsent(getKey(rule), key -> new ArrayDeque<>());

        synchronized (timestamps) {
            evictExpiredRequests(timestamps, currentTimeMs, rule.getWindowSizeMs());

            if (timestamps.size() >= rule.getLimit()) {
                long oldestRequestTime = timestamps.peekFirst();
                long retryAfterMs = rule.getWindowSizeMs() - (currentTimeMs - oldestRequestTime);
                return new RateLimitResult(false, 0, Math.max(1, retryAfterMs),
                        "Sliding window log limit exceeded");
            }

            timestamps.addLast(currentTimeMs);
            int remaining = rule.getLimit() - timestamps.size();
            return new RateLimitResult(true, remaining, 0,
                    "Request allowed by sliding window log");
        }
    }

    private void evictExpiredRequests(Deque<Long> timestamps, long currentTimeMs,
            long windowSizeMs) {
        while (!timestamps.isEmpty() && currentTimeMs - timestamps.peekFirst() >= windowSizeMs) {
            timestamps.removeFirst();
        }
    }

    private String getKey(RateLimitRuleEntity rule) {
        return rule.getClientId() + "::" + rule.getResourcePath();
    }
}
