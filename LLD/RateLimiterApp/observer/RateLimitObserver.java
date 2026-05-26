package LLD.RateLimiterApp.observer;

import LLD.RateLimiterApp.model.RateLimitResult;

public interface RateLimitObserver {
    void onDecision(String limiterName, String clientId, String resourcePath, int cost,
            RateLimitResult result);
}
