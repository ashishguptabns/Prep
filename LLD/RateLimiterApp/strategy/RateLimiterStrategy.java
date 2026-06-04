package LLD.RateLimiterApp.strategy;

import LLD.RateLimiterApp.model.RateLimitResult;

public interface RateLimiterStrategy {

    RateLimitResult evaluate(int cost, long now);
}
