package LLD.RateLimiterApp.model;

public class RateLimitResult {
    private final boolean allowed;
    private final int remainingLimit;
    private final long retryAfterMs;
    private final String reason;

    public RateLimitResult(boolean allowed, int remainingLimit, long retryAfterMs, String reason) {
        this.allowed = allowed;
        this.remainingLimit = remainingLimit;
        this.retryAfterMs = retryAfterMs;
        this.reason = reason;
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

    @Override
    public String toString() {
        return "RateLimitResult{allowed=" + allowed + ", remainingLimit=" + remainingLimit
                + ", retryAfterMs=" + retryAfterMs + ", reason='" + reason + "'}";
    }
}
