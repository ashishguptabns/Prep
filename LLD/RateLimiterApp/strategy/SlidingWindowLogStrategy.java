package LLD.RateLimiterApp.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.RequestStatus;
import LLD.RateLimiterApp.model.RuleConfig;

public class SlidingWindowLogStrategy implements RateLimiterStrategy {

    private final RuleConfig config;
    private final AtomicReference<List<Long>> logRef = new AtomicReference<>(Collections.emptyList());

    public SlidingWindowLogStrategy(RuleConfig config) {
        this.config = config;
    }

    @Override
    public RateLimitResult evaluate(int cost, long now) {
        long windowMs = config.getWindowSeconds() * 1000;
        long boundary = now - windowMs;

        while (true) {
            List<Long> currentLog = logRef.get();
            int startIdx = 0;
            while (startIdx < currentLog.size() && currentLog.get(startIdx) <= boundary) {
                startIdx++;
            }

            int currentCount = currentLog.size() - startIdx;
            if (currentCount + cost > config.getMaxRequests()) {
                long remaining = config.getMaxRequests() - currentCount;
                long oldestTimestamp = currentLog.get(startIdx);
                long retryAfter = oldestTimestamp + windowMs - now;
                return new RateLimitResult(RequestStatus.THROTTLED, now, config.getRuleName(), Math.max(0, remaining), Math.max(0, retryAfter));
            }

            List<Long> nextLog = new ArrayList<>(currentCount + cost);
            for (int i = startIdx; i < currentLog.size(); i++) {
                nextLog.add(currentLog.get(i));
            }
            for (int i = 0; i < cost; i++) {
                nextLog.add(now);
            }

            if (logRef.compareAndSet(currentLog, nextLog)) {
                return new RateLimitResult(RequestStatus.ALLOWED, now, null, config.getMaxRequests() - nextLog.size(), 0);
            }
        }
    }
}
