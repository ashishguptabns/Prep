package LLD.RateLimiterApp.model;

public class RuleConfig {

    private final String ruleName;
    private final StrategyType strategy;
    private final Scope scope;
    private final long maxRequests;
    private final long windowSeconds;
    private final double bucketCapacity;
    private final double refillRatePerSec;

    public RuleConfig(String ruleName, StrategyType strategy, Scope scope, long maxRequests, long windowSeconds, double bucketCapacity, double refillRatePerSec) {
        this.ruleName = ruleName;
        this.strategy = strategy;
        this.scope = scope;
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
        this.bucketCapacity = bucketCapacity;
        this.refillRatePerSec = refillRatePerSec;
    }

    public String getRuleName() {
        return ruleName;
    }

    public StrategyType getStrategy() {
        return strategy;
    }

    public Scope getScope() {
        return scope;
    }

    public long getMaxRequests() {
        return maxRequests;
    }

    public long getWindowSeconds() {
        return windowSeconds;
    }

    public double getBucketCapacity() {
        return bucketCapacity;
    }

    public double getRefillRatePerSec() {
        return refillRatePerSec;
    }
}
