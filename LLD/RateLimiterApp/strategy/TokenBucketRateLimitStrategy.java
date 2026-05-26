package LLD.RateLimiterApp.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public class TokenBucketRateLimitStrategy implements RateLimitStrategy {
    private final Map<String, AtomicReference<Bucket>> buckets = new ConcurrentHashMap<>();

    @Override
    public RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs) {
        AtomicReference<Bucket> bucketRef = buckets.computeIfAbsent(getKey(rule),
                key -> new AtomicReference<>(new Bucket(rule.getCapacity(), currentTimeMs)));

        while (true) {
            Bucket current = bucketRef.get();
            Bucket refilled = refill(current, rule, currentTimeMs);

            if (refilled.tokens < 1) {
                long retryAfterMs = Math.max(1, 1_000 / rule.getRefillRatePerSecond());
                return new RateLimitResult(false, 0, retryAfterMs,
                        "Token bucket limit exceeded");
            }

            Bucket next = new Bucket(refilled.tokens - 1, refilled.lastRefillTimeMs);
            if (bucketRef.compareAndSet(current, next)) {
                return new RateLimitResult(true, next.tokens, 0,
                        "Request allowed by token bucket");
            }
        }
    }

    @Override
    public void rollback(RateLimitRuleEntity rule, long currentTimeMs) {
        AtomicReference<Bucket> bucketRef = buckets.get(getKey(rule));
        if (bucketRef == null) {
            return;
        }

        while (true) {
            Bucket current = bucketRef.get();
            Bucket next = new Bucket(Math.min(rule.getCapacity(), current.tokens + 1),
                    current.lastRefillTimeMs);
            if (bucketRef.compareAndSet(current, next)) {
                return;
            }
        }
    }

    private Bucket refill(Bucket current, RateLimitRuleEntity rule, long currentTimeMs) {
        long elapsedMs = currentTimeMs - current.lastRefillTimeMs;
        int tokensToAdd = (int) ((elapsedMs * rule.getRefillRatePerSecond()) / 1_000);
        if (tokensToAdd <= 0) {
            return current;
        }

        int nextTokens = Math.min(rule.getCapacity(), current.tokens + tokensToAdd);
        long nextRefillTime = current.lastRefillTimeMs
                + ((long) tokensToAdd * 1_000 / rule.getRefillRatePerSecond());
        return new Bucket(nextTokens, nextRefillTime);
    }

    private String getKey(RateLimitRuleEntity rule) {
        return rule.getStateKey();
    }

    private static class Bucket {
        private final int tokens;
        private final long lastRefillTimeMs;

        private Bucket(int tokens, long lastRefillTimeMs) {
            this.tokens = tokens;
            this.lastRefillTimeMs = lastRefillTimeMs;
        }
    }
}
