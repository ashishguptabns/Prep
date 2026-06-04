package LLD.RateLimiterApp.strategy;

import java.util.concurrent.atomic.AtomicReference;

import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.RequestStatus;
import LLD.RateLimiterApp.model.RuleConfig;

class FixedWindowState {

    final long windowId;
    final long count;

    FixedWindowState(long windowId, long count) {
        this.windowId = windowId;
        this.count = count;
    }
}

public class FixedWindowStrategy implements RateLimiterStrategy {

    private final RuleConfig config;
    private final AtomicReference<FixedWindowState> stateRef = new AtomicReference<>(new FixedWindowState(0, 0));

    public FixedWindowStrategy(RuleConfig config) {
        this.config = config;
    }

    @Override
    public RateLimitResult evaluate(int cost, long now) {
        long windowMs = config.getWindowSeconds() * 1000;
        long currentWindowId = now / windowMs;
        long nextWindowStart = (currentWindowId + 1) * windowMs;

        while (true) {
            FixedWindowState current = stateRef.get();
            long newCount;
            if (current.windowId != currentWindowId) {
                newCount = cost;
            } else {
                newCount = current.count + cost;
            }

            if (newCount > config.getMaxRequests()) {
                long remaining = config.getMaxRequests() - (current.windowId == currentWindowId ? current.count : 0);
                long retryAfter = nextWindowStart - now;
                return new RateLimitResult(RequestStatus.THROTTLED, now, config.getRuleName(), Math.max(0, remaining), retryAfter);
            }

            if (stateRef.compareAndSet(current, new FixedWindowState(currentWindowId, newCount))) {
                return new RateLimitResult(RequestStatus.ALLOWED, now, null, config.getMaxRequests() - newCount, 0);
            }
        }
    }
}
