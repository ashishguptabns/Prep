package LLD.RateLimiterApp.strategy;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public interface RateLimitStrategy {
    RateLimitResult allow(RateLimitRuleEntity rule, long currentTimeMs);
}
