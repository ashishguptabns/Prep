package LLD.RateLimiterApp.service;

import java.util.ArrayList;
import java.util.List;

import LLD.RateLimiterApp.helper.RuleEvaluator;
import LLD.RateLimiterApp.model.LimiterConfig;
import LLD.RateLimiterApp.model.LimiterMode;
import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.Request;
import LLD.RateLimiterApp.model.RequestStatus;
import LLD.RateLimiterApp.model.RuleConfig;
import LLD.RateLimiterApp.observer.ObserverRegistry;
import LLD.RateLimiterApp.observer.RateLimitObserver;

public class RateLimiterService {

    private final LimiterConfig config;
    private final List<RuleEvaluator> evaluators;
    private final ObserverRegistry observerRegistry;
    private final Object mutex = new Object();

    public RateLimiterService(LimiterConfig config, List<RateLimitObserver> observers) {
        this.config = config;
        this.evaluators = new ArrayList<>();
        for (RuleConfig ruleConfig : config.getRules()) {
            this.evaluators.add(new RuleEvaluator(ruleConfig));
        }
        this.observerRegistry = new ObserverRegistry(observers);
    }

    public RateLimitResult check(Request request) {
        if (config.getMode() == LimiterMode.BLOCKING) {
            return checkBlocking(request);
        } else {
            return checkNonBlocking(request);
        }
    }

    private RateLimitResult checkNonBlocking(Request request) {
        long now = System.currentTimeMillis();
        RateLimitResult worstThrottledResult = null;
        long minRemainingQuota = Long.MAX_VALUE;

        for (RuleEvaluator evaluator : evaluators) {
            RateLimitResult result = evaluator.evaluate(request, now);
            if (result.getStatus() == RequestStatus.THROTTLED) {
                if (worstThrottledResult == null || result.getRetryAfterMs() > worstThrottledResult.getRetryAfterMs()) {
                    worstThrottledResult = result;
                }
            } else {
                if (result.getRemainingQuota() < minRemainingQuota) {
                    minRemainingQuota = result.getRemainingQuota();
                }
            }
        }

        RateLimitResult finalResult;
        if (worstThrottledResult != null) {
            finalResult = worstThrottledResult;
        } else {
            finalResult = new RateLimitResult(RequestStatus.ALLOWED, now, null, minRemainingQuota, 0);
        }

        observerRegistry.notifyObservers(config.getLimiterName(), request, finalResult);
        return finalResult;
    }

    private RateLimitResult checkBlocking(Request request) {
        long startTime = System.currentTimeMillis();
        long maxWaitTime = config.getMaxWaitTimeoutMs();

        synchronized (mutex) {
            while (true) {
                long now = System.currentTimeMillis();
                long elapsed = now - startTime;
                if (elapsed >= maxWaitTime) {
                    return checkNonBlocking(request);
                }

                RateLimitResult worstThrottledResult = null;
                long minRemainingQuota = Long.MAX_VALUE;

                for (RuleEvaluator evaluator : evaluators) {
                    RateLimitResult result = evaluator.evaluate(request, now);
                    if (result.getStatus() == RequestStatus.THROTTLED) {
                        if (worstThrottledResult == null || result.getRetryAfterMs() > worstThrottledResult.getRetryAfterMs()) {
                            worstThrottledResult = result;
                        }
                    } else {
                        if (result.getRemainingQuota() < minRemainingQuota) {
                            minRemainingQuota = result.getRemainingQuota();
                        }
                    }
                }

                if (worstThrottledResult == null) {
                    RateLimitResult allowedResult = new RateLimitResult(RequestStatus.ALLOWED, now, null, minRemainingQuota, 0);
                    observerRegistry.notifyObservers(config.getLimiterName(), request, allowedResult);
                    mutex.notifyAll();
                    return allowedResult;
                }

                long retryAfter = worstThrottledResult.getRetryAfterMs();
                if (retryAfter == Long.MAX_VALUE || retryAfter <= 0) {
                    retryAfter = 50;
                }

                long remainingWait = maxWaitTime - elapsed;
                long sleepTime = Math.min(retryAfter, remainingWait);

                if (sleepTime <= 0) {
                    observerRegistry.notifyObservers(config.getLimiterName(), request, worstThrottledResult);
                    return worstThrottledResult;
                }

                try {
                    mutex.wait(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return worstThrottledResult;
                }
            }
        }
    }

    public void cleanupState() {
        for (RuleEvaluator evaluator : evaluators) {
            evaluator.cleanupStaleKeys();
        }
    }
}
