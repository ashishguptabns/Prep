package LLD.RateLimiterApp.strategy;

import LLD.RateLimiterApp.entity.RuleEntity;
import LLD.RateLimiterApp.model.RateLimitResult;

public interface RateLimitStrategy {

    RateLimitResult allow(RuleEntity rule, long currentTimeMs);
}
