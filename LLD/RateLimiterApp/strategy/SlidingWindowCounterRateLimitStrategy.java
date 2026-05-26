package LLD.RateLimiterApp.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public class SlidingWindowCounterRateLimitStrategy implements RateLimitStrategy {
    private final Map<String, CounterWindow> windows = new ConcurrentHashMap<>();

    @Override
    public RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs) {
        CounterWindow window = windows.computeIfAbsent(getKey(rule),
                key -> new CounterWindow(getWindowStart(currentTimeMs, rule.getWindowSizeMs())));

        synchronized (window) {
            rotateWindowIfRequired(window, currentTimeMs, rule.getWindowSizeMs());

            double previousWeight = 1.0 - ((double) (currentTimeMs - window.currentWindowStartMs)
                    / rule.getWindowSizeMs());
            double estimatedCount = window.currentCount + (window.previousCount * previousWeight);

            if (estimatedCount >= rule.getLimit()) {
                long retryAfterMs = window.currentWindowStartMs + rule.getWindowSizeMs() - currentTimeMs;
                return new RateLimitResult(false, 0, Math.max(1, retryAfterMs),
                        "Sliding window counter limit exceeded");
            }

            window.currentCount++;
            int remaining = Math.max(0, rule.getLimit() - (int) Math.ceil(estimatedCount + 1));
            return new RateLimitResult(true, remaining, 0,
                    "Request allowed by sliding window counter");
        }
    }

    private void rotateWindowIfRequired(CounterWindow window, long currentTimeMs,
            long windowSizeMs) {
        long currentWindowStart = getWindowStart(currentTimeMs, windowSizeMs);
        if (currentWindowStart == window.currentWindowStartMs) {
            return;
        }

        long windowsPassed = (currentWindowStart - window.currentWindowStartMs) / windowSizeMs;
        window.previousCount = windowsPassed == 1 ? window.currentCount : 0;
        window.currentCount = 0;
        window.currentWindowStartMs = currentWindowStart;
    }

    private long getWindowStart(long currentTimeMs, long windowSizeMs) {
        return currentTimeMs - (currentTimeMs % windowSizeMs);
    }

    private String getKey(RateLimitRuleEntity rule) {
        return rule.getClientId() + "::" + rule.getResourcePath();
    }

    private static class CounterWindow {
        private long currentWindowStartMs;
        private int currentCount;
        private int previousCount;

        private CounterWindow(long currentWindowStartMs) {
            this.currentWindowStartMs = currentWindowStartMs;
        }
    }
}
