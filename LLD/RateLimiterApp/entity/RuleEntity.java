package LLD.RateLimiterApp.entity;

import LLD.RateLimiterApp.model.RateLimitAlgorithm;
import LLD.RateLimiterApp.model.RateLimitScope;

public class RuleEntity {

    private final String ruleId;
    private final String clientId;
    private final String resourcePath;
    private final RateLimitAlgorithm algorithm;
    private final RateLimitScope scope;
    private final int limit;
    private final long windowSizeMs;
    private final int capacity;
    private final int refillRatePerSecond;
    private final int leakRatePerSecond;

    public RuleEntity(RuleBuilder builder) {
        this.ruleId = builder.ruleId;
        this.clientId = builder.clientId;
        this.resourcePath = builder.resourcePath;
        this.algorithm = builder.algorithm;
        this.scope = builder.scope;
        this.limit = builder.limit;
        this.windowSizeMs = builder.windowSizeMs;
        this.capacity = builder.capacity;
        this.refillRatePerSecond = builder.refillRatePerSecond;
        this.leakRatePerSecond = builder.leakRatePerSecond;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public RateLimitAlgorithm getAlgorithm() {
        return algorithm;
    }

    public RateLimitScope getScope() {
        return scope;
    }

    public int getLimit() {
        return limit;
    }

    public long getWindowSizeMs() {
        return windowSizeMs;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRefillRatePerSecond() {
        return refillRatePerSecond;
    }

    public int getLeakRatePerSecond() {
        return leakRatePerSecond;
    }

    public String getStateKey() {
        String scopeKey = scope == RateLimitScope.GLOBAL ? "GLOBAL" : clientId;
        return ruleId + "::" + scope + "::" + scopeKey + "::" + resourcePath;
    }

    public static class RuleBuilder {

        private String ruleId;
        private String clientId;
        private String resourcePath;
        private RateLimitAlgorithm algorithm;
        private RateLimitScope scope;
        private int limit;
        private long windowSizeMs;
        private int capacity;
        private int refillRatePerSecond;
        private int leakRatePerSecond;

        public RuleBuilder user(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public RuleBuilder resource(String res) {
            this.resourcePath = res;
            return this;
        }

        public RuleBuilder algorithm(RateLimitAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public RuleBuilder scope(RateLimitScope scope) {
            this.scope = scope;
            return this;
        }

        public RuleBuilder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public RuleBuilder window(long window) {
            this.windowSizeMs = window;
            return this;
        }

        public RuleBuilder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public RuleBuilder refillRate(int refillRate) {
            this.refillRatePerSecond = refillRate;
            return this;
        }

        public RuleBuilder leakRate(int leakRate) {
            this.leakRatePerSecond = leakRate;
            return this;
        }

        public RuleEntity build() {
            return new RuleEntity(this);
        }

    }
}
