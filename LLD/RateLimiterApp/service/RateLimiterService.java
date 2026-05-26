package LLD.RateLimiterApp.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.entity.RequestLogEntity;
import LLD.RateLimiterApp.exception.RateLimiterException;
import LLD.RateLimiterApp.model.RateLimitAlgorithm;
import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.repository.RateLimitRuleStore;
import LLD.RateLimiterApp.repository.RequestLogStore;
import LLD.RateLimiterApp.strategy.FixedWindowRateLimitStrategy;
import LLD.RateLimiterApp.strategy.LeakyBucketRateLimitStrategy;
import LLD.RateLimiterApp.strategy.RateLimitStrategy;
import LLD.RateLimiterApp.strategy.SlidingWindowCounterRateLimitStrategy;
import LLD.RateLimiterApp.strategy.SlidingWindowLogRateLimitStrategy;
import LLD.RateLimiterApp.strategy.TokenBucketRateLimitStrategy;

public class RateLimiterService {
    private final RateLimitRuleStore ruleStore;
    private final RequestLogStore requestLogStore;
    private final Map<RateLimitAlgorithm, RateLimitStrategy> strategies;

    public RateLimiterService(RateLimitRuleStore ruleStore, RequestLogStore requestLogStore) {
        this(ruleStore, requestLogStore, defaultStrategies());
    }

    public RateLimiterService(RateLimitRuleStore ruleStore, RequestLogStore requestLogStore,
            Map<RateLimitAlgorithm, RateLimitStrategy> strategies) {
        this.ruleStore = ruleStore;
        this.requestLogStore = requestLogStore;
        this.strategies = strategies;
    }

    public RateLimitRuleEntity createRule(String clientId, String resourcePath,
            RateLimitAlgorithm algorithm, int limit, long windowSizeMs, int capacity,
            int refillRatePerSecond, int leakRatePerSecond) {
        validateRule(clientId, resourcePath, algorithm, limit, windowSizeMs, capacity,
                refillRatePerSecond, leakRatePerSecond);

        RateLimitRuleEntity rule = new RateLimitRuleEntity(clientId, resourcePath, algorithm,
                limit, windowSizeMs, capacity, refillRatePerSecond, leakRatePerSecond);
        ruleStore.save(rule);
        return rule;
    }

    public RateLimitResult allowRequest(String clientId, String resourcePath) {
        RateLimitRuleEntity rule = findRule(clientId, resourcePath);
        RateLimitStrategy strategy = strategies.get(rule.getAlgorithm());
        if (strategy == null) {
            throw new RateLimiterException("Strategy not configured: " + rule.getAlgorithm());
        }

        long currentTime = System.currentTimeMillis();
        RateLimitResult result = strategy.allow(rule, currentTime);
        requestLogStore.save(new RequestLogEntity(clientId, resourcePath, rule.getAlgorithm(),
                result.isAllowed(), currentTime, result.getReason()));
        return result;
    }

    public List<RequestLogEntity> getRequestHistory(String clientId, String resourcePath) {
        findRule(clientId, resourcePath);
        return requestLogStore.findByClientAndResource(clientId, resourcePath);
    }

    public RateLimitSummary getSummary(String clientId, String resourcePath) {
        RateLimitRuleEntity rule = findRule(clientId, resourcePath);
        List<RequestLogEntity> logs = requestLogStore.findByClientAndResource(clientId, resourcePath);

        int allowedRequests = 0;
        int blockedRequests = 0;
        for (RequestLogEntity log : logs) {
            if (log.isAllowed()) {
                allowedRequests++;
            } else {
                blockedRequests++;
            }
        }

        return new RateLimitSummary(rule.getRuleId(), rule.getAlgorithm(), logs.size(),
                allowedRequests, blockedRequests);
    }

    private RateLimitRuleEntity findRule(String clientId, String resourcePath) {
        return ruleStore.findByClientAndResource(clientId, resourcePath)
                .orElseThrow(() -> new RateLimiterException(
                        "Rate limit rule not found for " + clientId + " and " + resourcePath));
    }

    private void validateRule(String clientId, String resourcePath, RateLimitAlgorithm algorithm,
            int limit, long windowSizeMs, int capacity, int refillRatePerSecond,
            int leakRatePerSecond) {
        if (clientId == null || clientId.isBlank()) {
            throw new RateLimiterException("Client id is required");
        }
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new RateLimiterException("Resource path is required");
        }
        if (algorithm == null) {
            throw new RateLimiterException("Rate limit algorithm is required");
        }
        if (limit <= 0 || windowSizeMs <= 0 || capacity <= 0) {
            throw new RateLimiterException("Limit, window size, and capacity must be positive");
        }
        if (refillRatePerSecond <= 0 || leakRatePerSecond <= 0) {
            throw new RateLimiterException("Refill rate and leak rate must be positive");
        }
    }

    private static Map<RateLimitAlgorithm, RateLimitStrategy> defaultStrategies() {
        Map<RateLimitAlgorithm, RateLimitStrategy> strategies = new EnumMap<>(RateLimitAlgorithm.class);
        strategies.put(RateLimitAlgorithm.FIXED_WINDOW, new FixedWindowRateLimitStrategy());
        strategies.put(RateLimitAlgorithm.SLIDING_WINDOW_LOG, new SlidingWindowLogRateLimitStrategy());
        strategies.put(RateLimitAlgorithm.SLIDING_WINDOW_COUNTER,
                new SlidingWindowCounterRateLimitStrategy());
        strategies.put(RateLimitAlgorithm.TOKEN_BUCKET, new TokenBucketRateLimitStrategy());
        strategies.put(RateLimitAlgorithm.LEAKY_BUCKET, new LeakyBucketRateLimitStrategy());
        return strategies;
    }

    public static class RateLimitSummary {
        public final String ruleId;
        public final RateLimitAlgorithm algorithm;
        public final int totalRequests;
        public final int allowedRequests;
        public final int blockedRequests;

        public RateLimitSummary(String ruleId, RateLimitAlgorithm algorithm, int totalRequests,
                int allowedRequests, int blockedRequests) {
            this.ruleId = ruleId;
            this.algorithm = algorithm;
            this.totalRequests = totalRequests;
            this.allowedRequests = allowedRequests;
            this.blockedRequests = blockedRequests;
        }

        @Override
        public String toString() {
            return "RateLimitSummary{ruleId='" + ruleId + "', algorithm=" + algorithm
                    + ", totalRequests=" + totalRequests + ", allowedRequests="
                    + allowedRequests + ", blockedRequests=" + blockedRequests + "}";
        }
    }
}
