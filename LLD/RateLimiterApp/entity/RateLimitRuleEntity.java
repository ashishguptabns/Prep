package LLD.RateLimiterApp.entity;

import java.util.UUID;

import LLD.RateLimiterApp.model.RateLimitAlgorithm;
import LLD.RateLimiterApp.model.RateLimitScope;

public class RateLimitRuleEntity {
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

    public RateLimitRuleEntity(String clientId, String resourcePath,
            RateLimitAlgorithm algorithm, int limit, long windowSizeMs, int capacity,
            int refillRatePerSecond, int leakRatePerSecond) {
        this(clientId, resourcePath, algorithm, RateLimitScope.PER_KEY, limit, windowSizeMs,
                capacity, refillRatePerSecond, leakRatePerSecond);
    }

    public RateLimitRuleEntity(String clientId, String resourcePath,
            RateLimitAlgorithm algorithm, RateLimitScope scope, int limit, long windowSizeMs,
            int capacity, int refillRatePerSecond, int leakRatePerSecond) {
        this(UUID.randomUUID().toString(), clientId, resourcePath, algorithm, scope, limit,
                windowSizeMs, capacity, refillRatePerSecond, leakRatePerSecond);
    }

    private RateLimitRuleEntity(String ruleId, String clientId, String resourcePath,
            RateLimitAlgorithm algorithm, RateLimitScope scope, int limit, long windowSizeMs,
            int capacity, int refillRatePerSecond, int leakRatePerSecond) {
        this.ruleId = ruleId;
        this.clientId = clientId;
        this.resourcePath = resourcePath;
        this.algorithm = algorithm;
        this.scope = scope;
        this.limit = limit;
        this.windowSizeMs = windowSizeMs;
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.leakRatePerSecond = leakRatePerSecond;
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

    public RateLimitRuleEntity forRequestClient(String requestClientId) {
        if (scope == RateLimitScope.GLOBAL || clientId.equals(requestClientId)) {
            return this;
        }
        return new RateLimitRuleEntity(ruleId, requestClientId, resourcePath, algorithm, scope,
                limit, windowSizeMs, capacity, refillRatePerSecond, leakRatePerSecond);
    }

    public String getStateKey() {
        String scopeKey = scope == RateLimitScope.GLOBAL ? "GLOBAL" : clientId;
        return ruleId + "::" + scope + "::" + scopeKey + "::" + resourcePath;
    }

    @Override
    public String toString() {
        return "RateLimitRuleEntity{ruleId='" + ruleId + "', clientId='" + clientId
                + "', resourcePath='" + resourcePath + "', algorithm=" + algorithm
                + ", scope=" + scope + ", limit=" + limit + ", windowSizeMs=" + windowSizeMs
                + ", capacity=" + capacity + ", refillRatePerSecond=" + refillRatePerSecond
                + ", leakRatePerSecond=" + leakRatePerSecond + "}";
    }
}
