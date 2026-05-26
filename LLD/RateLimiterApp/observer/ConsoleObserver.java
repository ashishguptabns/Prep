package LLD.RateLimiterApp.observer;

import java.time.Instant;

import LLD.RateLimiterApp.model.RateLimitResult;

public class ConsoleObserver implements RateLimitObserver {

    @Override
    public void onDecision(String clientId, String resourcePath,
            RateLimitResult result) {
        System.out.println(Instant.ofEpochMilli(result.getEvaluatedAtMs()) + " ["
                + clientId + " status="
                + (result.isAllowed() ? "ALLOWED" : "THROTTLED") + " rule="
                + result.getThrottledByRule() + " remaining=" + result.getRemainingLimit()
                + " retryAfterMs=" + result.getRetryAfterMs());
    }
}
