package LLD.RateLimiterApp.strategy;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public interface RateLimitStrategy {
    RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs);

    default RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs, int cost) {
        return allow(rule, currentTimeMs);
    }

    default void rollback(RateLimitRuleEntity rule, long currentTimeMs) {
        rollback(rule, currentTimeMs, 1);
    }

    default void rollback(RateLimitRuleEntity rule, long currentTimeMs, int cost) {
    }
}
