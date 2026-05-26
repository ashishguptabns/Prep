package LLD.RateLimiterApp.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public class SlidingWindowCounterRateLimitStrategy implements RateLimitStrategy {
    private final Map<String, AtomicReference<CounterWindow>> windows = new ConcurrentHashMap<>();

    @Override
    public RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs) {
        AtomicReference<CounterWindow> windowRef = windows.computeIfAbsent(getKey(rule),
                key -> new AtomicReference<>(
                        new CounterWindow(getWindowStart(currentTimeMs, rule.getWindowSizeMs()), 0, 0)));

        while (true) {
            CounterWindow current = windowRef.get();
            CounterWindow rotated = rotateWindowIfRequired(current, currentTimeMs,
                    rule.getWindowSizeMs());

            double previousWeight = 1.0 - ((double) (currentTimeMs - rotated.currentWindowStartMs)
                    / rule.getWindowSizeMs());
            double estimatedCount = rotated.currentCount + (rotated.previousCount * previousWeight);

            if (estimatedCount >= rule.getLimit()) {
                long retryAfterMs = rotated.currentWindowStartMs + rule.getWindowSizeMs() - currentTimeMs;
                return new RateLimitResult(false, 0, Math.max(1, retryAfterMs),
                        "Sliding window counter limit exceeded");
            }

            CounterWindow next = new CounterWindow(rotated.currentWindowStartMs,
                    rotated.currentCount + 1, rotated.previousCount);
            if (windowRef.compareAndSet(current, next)) {
                int remaining = Math.max(0, rule.getLimit() - (int) Math.ceil(estimatedCount + 1));
                return new RateLimitResult(true, remaining, 0,
                        "Request allowed by sliding window counter");
            }
        }
    }

    @Override
    public void rollback(RateLimitRuleEntity rule, long currentTimeMs) {
        AtomicReference<CounterWindow> windowRef = windows.get(getKey(rule));
        if (windowRef == null) {
            return;
        }

        while (true) {
            CounterWindow current = windowRef.get();
            if (currentTimeMs - current.currentWindowStartMs >= rule.getWindowSizeMs()) {
                return;
            }
            CounterWindow next = new CounterWindow(current.currentWindowStartMs,
                    Math.max(0, current.currentCount - 1), current.previousCount);
            if (windowRef.compareAndSet(current, next)) {
                return;
            }
        }
    }

    private CounterWindow rotateWindowIfRequired(CounterWindow window, long currentTimeMs,
            long windowSizeMs) {
        long currentWindowStart = getWindowStart(currentTimeMs, windowSizeMs);
        if (currentWindowStart == window.currentWindowStartMs) {
            return window;
        }

        long windowsPassed = (currentWindowStart - window.currentWindowStartMs) / windowSizeMs;
        int previousCount = windowsPassed == 1 ? window.currentCount : 0;
        return new CounterWindow(currentWindowStart, 0, previousCount);
    }

    private long getWindowStart(long currentTimeMs, long windowSizeMs) {
        return currentTimeMs - (currentTimeMs % windowSizeMs);
    }

    private String getKey(RateLimitRuleEntity rule) {
        return rule.getStateKey();
    }

    private static class CounterWindow {
        private final long currentWindowStartMs;
        private final int currentCount;
        private final int previousCount;

        private CounterWindow(long currentWindowStartMs, int currentCount, int previousCount) {
            this.currentWindowStartMs = currentWindowStartMs;
            this.currentCount = currentCount;
            this.previousCount = previousCount;
        }
    }
}
