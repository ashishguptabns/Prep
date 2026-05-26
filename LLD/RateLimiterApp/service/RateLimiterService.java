package LLD.RateLimiterApp.service;

import java.util.EnumMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.entity.RequestLogEntity;
import LLD.RateLimiterApp.exception.RateLimiterException;
import LLD.RateLimiterApp.model.RateLimitAlgorithm;
import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.RateLimitScope;
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
        return createRule(clientId, resourcePath, algorithm, RateLimitScope.PER_KEY, limit,
                windowSizeMs, capacity, refillRatePerSecond, leakRatePerSecond);
    }

    public RateLimitRuleEntity createRule(String clientId, String resourcePath,
            RateLimitAlgorithm algorithm, RateLimitScope scope, int limit, long windowSizeMs,
            int capacity, int refillRatePerSecond, int leakRatePerSecond) {
        validateRule(clientId, resourcePath, algorithm, scope, limit, windowSizeMs, capacity,
                refillRatePerSecond, leakRatePerSecond);

        RateLimitRuleEntity rule = new RateLimitRuleEntity(clientId, resourcePath, algorithm,
                scope, limit, windowSizeMs, capacity, refillRatePerSecond, leakRatePerSecond);
        ruleStore.save(rule);
        return rule;
    }

    public RateLimitResult allowRequest(String clientId, String resourcePath) {
        List<RateLimitRuleEntity> rules = findRules(clientId, resourcePath);
        long currentTime = System.currentTimeMillis();
        int remainingLimit = Integer.MAX_VALUE;
        List<AcceptedRule> acceptedRules = new ArrayList<>();

        for (RateLimitRuleEntity rule : rules) {
            RateLimitRuleEntity evaluationRule = rule.forRequestClient(clientId);
            RateLimitStrategy strategy = strategies.get(rule.getAlgorithm());
            if (strategy == null) {
                throw new RateLimiterException("Strategy not configured: " + rule.getAlgorithm());
            }

            RateLimitResult ruleResult = strategy.allow(evaluationRule, currentTime);
            if (!ruleResult.isAllowed()) {
                rollbackAcceptedRules(acceptedRules, currentTime);
                RateLimitResult result = new RateLimitResult(false, 0,
                        ruleResult.getRetryAfterMs(),
                        "Blocked by " + rule.getScope() + " rule " + rule.getRuleId() + ": "
                                + ruleResult.getReason());
                requestLogStore.save(new RequestLogEntity(clientId, resourcePath, rule.getAlgorithm(),
                        false, currentTime, result.getReason()));
                return result;
            }

            acceptedRules.add(new AcceptedRule(evaluationRule, strategy));
            remainingLimit = Math.min(remainingLimit, ruleResult.getRemainingLimit());
        }

        RateLimitRuleEntity primaryRule = rules.get(0);
        RateLimitResult result = new RateLimitResult(true, remainingLimit, 0,
                "Request allowed by all matching rules");
        requestLogStore.save(new RequestLogEntity(clientId, resourcePath, primaryRule.getAlgorithm(),
                result.isAllowed(), currentTime, result.getReason()));
        return result;
    }

    private void rollbackAcceptedRules(List<AcceptedRule> acceptedRules, long currentTime) {
        for (int i = acceptedRules.size() - 1; i >= 0; i--) {
            AcceptedRule acceptedRule = acceptedRules.get(i);
            acceptedRule.strategy.rollback(acceptedRule.rule, currentTime);
        }
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

    private List<RateLimitRuleEntity> findRules(String clientId, String resourcePath) {
        List<RateLimitRuleEntity> rules = ruleStore.findAllByClientAndResource(clientId, resourcePath);
        if (rules.isEmpty()) {
            throw new RateLimiterException(
                    "Rate limit rule not found for " + clientId + " and " + resourcePath);
        }
        rules.sort((left, right) -> left.getScope().compareTo(right.getScope()));
        return rules;
    }

    private void validateRule(String clientId, String resourcePath, RateLimitAlgorithm algorithm,
            RateLimitScope scope, int limit, long windowSizeMs, int capacity, int refillRatePerSecond,
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
        if (scope == null) {
            throw new RateLimiterException("Rate limit scope is required");
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

    private static class AcceptedRule {
        private final RateLimitRuleEntity rule;
        private final RateLimitStrategy strategy;

        private AcceptedRule(RateLimitRuleEntity rule, RateLimitStrategy strategy) {
            this.rule = rule;
            this.strategy = strategy;
        }
    }
}
