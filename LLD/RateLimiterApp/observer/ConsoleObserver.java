package LLD.RateLimiterApp.observer;

import java.time.Instant;

import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.Request;
import LLD.RateLimiterApp.model.RequestStatus;

public class ConsoleObserver implements RateLimitObserver {

    @Override
    public void onDecision(String limiterName, Request request, RateLimitResult result) {
        String timestamp = Instant.ofEpochMilli(result.getEvaluatedAt()).toString();
        if (result.getStatus() == RequestStatus.ALLOWED) {
            System.out.printf("%s [%s] key=%s cost=%d status=%s remaining=%d retryAfterMs=%d%n",
                    timestamp, limiterName, request.getKey(), request.getCost(),
                    result.getStatus(), result.getRemainingQuota(), result.getRetryAfterMs());
        } else {
            System.out.printf("%s [%s] key=%s cost=%d status=%s rule=%s remaining=%d retryAfterMs=%d%n",
                    timestamp, limiterName, request.getKey(), request.getCost(),
                    result.getStatus(), result.getThrottledByRule(), result.getRemainingQuota(), result.getRetryAfterMs());
        }
    }
}
