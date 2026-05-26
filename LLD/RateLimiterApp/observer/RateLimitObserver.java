package LLD.RateLimiterApp.observer;

import LLD.RateLimiterApp.model.RateLimitResult;

public interface RateLimitObserver {
    void onDecision(String clientId, String resourcePath, 
            RateLimitResult result);
}
