package LLD.RateLimiterApp.model;

public class RateLimitResult {
    private final boolean allowed;
    private final int remainingLimit;
    private final long retryAfterMs;
    private final String reason;
    private final long evaluatedAtMs;
    private final String throttledByRule;

    public RateLimitResult(boolean allowed, int remainingLimit, long retryAfterMs, String reason) {
        this(allowed, remainingLimit, retryAfterMs, reason, System.currentTimeMillis(), null);
    }

    public RateLimitResult(boolean allowed, int remainingLimit, long retryAfterMs, String reason,
            long evaluatedAtMs, String throttledByRule) {
        this.allowed = allowed;
        this.remainingLimit = remainingLimit;
        this.retryAfterMs = retryAfterMs;
        this.reason = reason;
        this.evaluatedAtMs = evaluatedAtMs;
        this.throttledByRule = throttledByRule;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getRemainingLimit() {
        return remainingLimit;
    }

    public long getRetryAfterMs() {
        return retryAfterMs;
    }

    public String getReason() {
        return reason;
    }

    public long getEvaluatedAtMs() {
        return evaluatedAtMs;
    }

    public String getThrottledByRule() {
        return throttledByRule;
    }

    @Override
    public String toString() {
        return "RateLimitResult{status=" + (allowed ? "ALLOWED" : "THROTTLED")
                + ", evaluatedAtMs=" + evaluatedAtMs + ", throttledByRule="
                + throttledByRule + ", remainingLimit=" + remainingLimit + ", retryAfterMs="
                + retryAfterMs + ", reason='" + reason + "'}";
    }
}
