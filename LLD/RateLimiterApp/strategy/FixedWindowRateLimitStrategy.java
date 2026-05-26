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
        return allow(rule, currentTimeMs, 1);
    }

    @Override
    public RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs, int cost) {
        Window window = windows.compute(getKey(rule), (key, existing) -> {
            if (existing == null || currentTimeMs - existing.getStartTimeMs() >= rule.getWindowSizeMs()) {
                return new Window(currentTimeMs);
            }
            return existing;
        });

        synchronized (window) {
            if (window.getRequestCount() + cost <= rule.getLimit()) {
                int requestCount = window.addAndGet(cost);
                int remaining = Math.max(0, rule.getLimit() - requestCount);
                return new RateLimitResult(true, remaining, 0, "Request allowed by fixed window");
            }

            long retryAfterMs = rule.getWindowSizeMs() - (currentTimeMs - window.getStartTimeMs());
            return new RateLimitResult(false, Math.max(0, rule.getLimit() - window.getRequestCount()),
                    Math.max(1, retryAfterMs), "Fixed window limit exceeded");
        }
    }

    @Override
    public void rollback(RateLimitRuleEntity rule, long currentTimeMs) {
        rollback(rule, currentTimeMs, 1);
    }

    @Override
    public void rollback(RateLimitRuleEntity rule, long currentTimeMs, int cost) {
        Window window = windows.get(getKey(rule));
        if (window != null && currentTimeMs - window.getStartTimeMs() < rule.getWindowSizeMs()) {
            synchronized (window) {
                window.subtract(cost);
            }
        }
    }

    private String getKey(RateLimitRuleEntity rule) {
        return rule.getStateKey();
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

        private int getRequestCount() {
            return requestCount.get();
        }

        private int addAndGet(int cost) {
            return requestCount.addAndGet(cost);
        }

        private void subtract(int cost) {
            requestCount.updateAndGet(count -> Math.max(0, count - cost));
        }
    }
}
