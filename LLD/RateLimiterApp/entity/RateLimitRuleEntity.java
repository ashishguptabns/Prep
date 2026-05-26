package LLD.RateLimiterApp.entity;

import java.util.UUID;

import LLD.RateLimiterApp.model.RateLimitAlgorithm;

public class RateLimitRuleEntity {
    private final String ruleId;
    private final String clientId;
    private final String resourcePath;
    private final RateLimitAlgorithm algorithm;
    private final int limit;
    private final long windowSizeMs;
    private final int capacity;
    private final int refillRatePerSecond;
    private final int leakRatePerSecond;

    public RateLimitRuleEntity(String clientId, String resourcePath,
            RateLimitAlgorithm algorithm, int limit, long windowSizeMs, int capacity,
            int refillRatePerSecond, int leakRatePerSecond) {
        this.ruleId = UUID.randomUUID().toString();
        this.clientId = clientId;
        this.resourcePath = resourcePath;
        this.algorithm = algorithm;
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

    @Override
    public String toString() {
        return "RateLimitRuleEntity{ruleId='" + ruleId + "', clientId='" + clientId
                + "', resourcePath='" + resourcePath + "', algorithm=" + algorithm
                + ", limit=" + limit + ", windowSizeMs=" + windowSizeMs
                + ", capacity=" + capacity + ", refillRatePerSecond=" + refillRatePerSecond
                + ", leakRatePerSecond=" + leakRatePerSecond + "}";
    }
}
