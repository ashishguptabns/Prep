package LLD.RateLimiterApp.observer;

import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.Request;

public interface RateLimitObserver {

    void onDecision(String limiterName, Request request, RateLimitResult result);
}
