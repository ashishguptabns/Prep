package LLD.RateLimiterApp.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import LLD.RateLimiterApp.entity.RequestLogEntity;
import LLD.RateLimiterApp.entity.RuleEntity;
import LLD.RateLimiterApp.exception.RateLimiterException;
import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.observer.RateLimitObserver;
import LLD.RateLimiterApp.repository.RequestLogRepository;
import LLD.RateLimiterApp.repository.RequestLogStore;
import LLD.RateLimiterApp.strategy.RateLimitStrategy;

public class RateLimiterService {

    private final RequestLogStore requestLogStore;
    private final List<RateLimitObserver> observers = new CopyOnWriteArrayList<>();
    private final ExecutorService observerExecutor = Executors.newSingleThreadExecutor();
    final RateLimitStrategy strategy;

    public RateLimiterService(RateLimitStrategy strategy, RequestLogRepository requestLogRepository) {
        this.requestLogStore = requestLogRepository;
        this.strategy = strategy;
    }

    public void registerObserver(RateLimitObserver observer) {
        observers.add(observer);
    }

    public RateLimitResult allowRequest(String clientId, String resourcePath, List<RuleEntity> rules) {
        long currentTime = System.currentTimeMillis();
        int remainingLimit = Integer.MAX_VALUE;

        for (RuleEntity rule : rules) {
            if (strategy == null) {
                throw new RateLimiterException("Strategy not configured: " + rule.getAlgorithm());
            }

            RateLimitResult ruleResult = strategy.allow(rule, currentTime);
            if (!ruleResult.isAllowed()) {
                RateLimitResult result = new RateLimitResult(false, 0,
                        ruleResult.getRetryAfterMs(),
                        "Blocked by " + rule.getScope() + " rule " + rule.getRuleId() + ": "
                        + ruleResult.getReason(),
                        currentTime, rule.getRuleId());
                requestLogStore.save(new RequestLogEntity(clientId, resourcePath, rule.getAlgorithm(),
                        false, currentTime, result.getReason()));
                notifyObservers(clientId, resourcePath, result);
                return result;
            }

            remainingLimit = Math.min(remainingLimit, ruleResult.getRemainingLimit());
        }

        RuleEntity primaryRule = rules.get(0);
        RateLimitResult result = new RateLimitResult(true, remainingLimit, 0,
                "Request allowed by all matching rules", currentTime, null);
        requestLogStore.save(new RequestLogEntity(clientId, resourcePath, primaryRule.getAlgorithm(),
                result.isAllowed(), currentTime, result.getReason()));
        notifyObservers(clientId, resourcePath, result);
        return result;
    }

    public RateLimitResult allowRequestBlocking(String clientId, String resourcePath,
            long maxWaitTimeoutMs, List<RuleEntity> rules) {
        long deadline = System.currentTimeMillis() + maxWaitTimeoutMs;
        while (true) {
            RateLimitResult result = allowRequest(clientId, resourcePath, rules);
            if (result.isAllowed() || result.getRetryAfterMs() == 0) {
                return result;
            }

            long remainingWaitMs = deadline - System.currentTimeMillis();
            if (remainingWaitMs <= 0) {
                return new RateLimitResult(false, result.getRemainingLimit(), result.getRetryAfterMs(),
                        "Blocking wait timed out: " + result.getReason(),
                        System.currentTimeMillis(), result.getThrottledByRule());
            }

            try {
                Thread.sleep(Math.max(1, Math.min(result.getRetryAfterMs(), remainingWaitMs)));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new RateLimitResult(false, result.getRemainingLimit(), result.getRetryAfterMs(),
                        "Blocking wait interrupted", System.currentTimeMillis(),
                        result.getThrottledByRule());
            }
        }
    }

    public void shutdown() {
        observerExecutor.shutdown();
    }

    private void notifyObservers(String clientId, String resourcePath,
            RateLimitResult result) {
        for (RateLimitObserver observer : observers) {
            observerExecutor.submit(() -> observer.onDecision(clientId, resourcePath,
                    result));
        }
    }

    public List<RequestLogEntity> getRequestHistory(String clientId, String resourcePath) {
        return requestLogStore.findByClientAndResource(clientId, resourcePath);
    }

}
