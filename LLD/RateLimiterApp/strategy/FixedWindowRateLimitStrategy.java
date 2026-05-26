package LLD.RateLimiterApp.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public class FixedWindowRateLimitStrategy implements RateLimitStrategy {
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    public RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs) {
        Window window = windows.compute(getKey(rule), (key, existing) -> {
            if (existing == null || currentTimeMs - existing.getStartTimeMs() >= rule.getWindowSizeMs()) {
                return new Window(currentTimeMs);
            }
            return existing;
        });

        int requestCount = window.incrementAndGet();
        int remaining = Math.max(0, rule.getLimit() - requestCount);
        if (requestCount <= rule.getLimit()) {
            return new RateLimitResult(true, remaining, 0, "Request allowed by fixed window");
        }

        long retryAfterMs = rule.getWindowSizeMs() - (currentTimeMs - window.getStartTimeMs());
        return new RateLimitResult(false, 0, Math.max(1, retryAfterMs),
                "Fixed window limit exceeded");
    }

    private String getKey(RateLimitRuleEntity rule) {
        return rule.getClientId() + "::" + rule.getResourcePath();
    }

    private static class Window {
        private final long startTimeMs;
        private final AtomicInteger requestCount = new AtomicInteger(0);

        private Window(long startTimeMs) {
            this.startTimeMs = startTimeMs;
        }

        private long getStartTimeMs() {
            return startTimeMs;
        }

        private int incrementAndGet() {
            return requestCount.incrementAndGet();
        }
    }
}
