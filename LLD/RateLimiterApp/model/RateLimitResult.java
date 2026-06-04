package LLD.RateLimiterApp.model;

public class RateLimitResult {

    private final RequestStatus status;
    private final long evaluatedAt;
    private final String throttledByRule;
    private final long remainingQuota;
    private final long retryAfterMs;

    public RateLimitResult(RequestStatus status, long evaluatedAt, String throttledByRule, long remainingQuota, long retryAfterMs) {
        this.status = status;
        this.evaluatedAt = evaluatedAt;
        this.throttledByRule = throttledByRule;
        this.remainingQuota = remainingQuota;
        this.retryAfterMs = retryAfterMs;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public long getEvaluatedAt() {
        return evaluatedAt;
    }

    public String getThrottledByRule() {
        return throttledByRule;
    }

    public long getRemainingQuota() {
        return remainingQuota;
    }

    public long getRetryAfterMs() {
        return retryAfterMs;
    }
}
