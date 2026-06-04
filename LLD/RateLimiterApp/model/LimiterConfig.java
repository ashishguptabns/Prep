package LLD.RateLimiterApp.model;

import java.util.List;

public class LimiterConfig {

    private final String limiterName;
    private final LimiterMode mode;
    private final long maxWaitTimeoutMs;
    private final List<RuleConfig> rules;
    private final List<String> observerTypes;

    public LimiterConfig(String limiterName, LimiterMode mode, long maxWaitTimeoutMs, List<RuleConfig> rules, List<String> observerTypes) {
        this.limiterName = limiterName;
        this.mode = mode;
        this.maxWaitTimeoutMs = maxWaitTimeoutMs;
        this.rules = rules;
        this.observerTypes = observerTypes;
    }

    public String getLimiterName() {
        return limiterName;
    }

    public LimiterMode getMode() {
        return mode;
    }

    public long getMaxWaitTimeoutMs() {
        return maxWaitTimeoutMs;
    }

    public List<RuleConfig> getRules() {
        return rules;
    }

    public List<String> getObserverTypes() {
        return observerTypes;
    }
}
