package LLD.RateLimiterApp.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public class LeakyBucketRateLimitStrategy implements RateLimitStrategy {
    private final Map<String, AtomicReference<Bucket>> buckets = new ConcurrentHashMap<>();

    @Override
    public RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs) {
        AtomicReference<Bucket> bucketRef = buckets.computeIfAbsent(getKey(rule),
                key -> new AtomicReference<>(new Bucket(0, currentTimeMs)));

        while (true) {
            Bucket current = bucketRef.get();
            Bucket leaked = leak(current, rule, currentTimeMs);

            if (leaked.waterLevel >= rule.getCapacity()) {
                long retryAfterMs = Math.max(1, 1_000 / rule.getLeakRatePerSecond());
                return new RateLimitResult(false, 0, retryAfterMs,
                        "Leaky bucket limit exceeded");
            }

            Bucket next = new Bucket(leaked.waterLevel + 1, leaked.lastLeakTimeMs);
            if (bucketRef.compareAndSet(current, next)) {
                int remaining = rule.getCapacity() - next.waterLevel;
                return new RateLimitResult(true, remaining, 0,
                        "Request allowed by leaky bucket");
            }
        }
    }

    private Bucket leak(Bucket current, RateLimitRuleEntity rule, long currentTimeMs) {
        long elapsedMs = currentTimeMs - current.lastLeakTimeMs;
        int leakedRequests = (int) ((elapsedMs * rule.getLeakRatePerSecond()) / 1_000);
        if (leakedRequests <= 0) {
            return current;
        }

        int nextWaterLevel = Math.max(0, current.waterLevel - leakedRequests);
        long nextLeakTime = current.lastLeakTimeMs
                + ((long) leakedRequests * 1_000 / rule.getLeakRatePerSecond());
        return new Bucket(nextWaterLevel, nextLeakTime);
    }

    private String getKey(RateLimitRuleEntity rule) {
        return rule.getRuleId() + "::" + rule.getClientId() + "::" + rule.getResourcePath();
    }

    private static class Bucket {
        private final int waterLevel;
        private final long lastLeakTimeMs;

        private Bucket(int waterLevel, long lastLeakTimeMs) {
            this.waterLevel = waterLevel;
            this.lastLeakTimeMs = lastLeakTimeMs;
        }
    }
}
