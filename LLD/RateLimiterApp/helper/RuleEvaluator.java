package LLD.RateLimiterApp.helper;

import java.util.concurrent.ConcurrentHashMap;

import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.Request;
import LLD.RateLimiterApp.model.RuleConfig;
import LLD.RateLimiterApp.model.Scope;
import LLD.RateLimiterApp.strategy.FixedWindowStrategy;
import LLD.RateLimiterApp.strategy.RateLimiterStrategy;
import LLD.RateLimiterApp.strategy.SlidingWindowLogStrategy;
import LLD.RateLimiterApp.strategy.TokenBucketStrategy;

public class RuleEvaluator {

    private final RuleConfig config;
    private final RateLimiterStrategy globalStrategy;
    private final ConcurrentHashMap<String, RateLimiterStrategy> perKeyStrategies = new ConcurrentHashMap<>();

    public RuleEvaluator(RuleConfig config) {
        this.config = config;
        if (config.getScope() == Scope.GLOBAL) {
            this.globalStrategy = createStrategy(config);
        } else {
            this.globalStrategy = null;
        }
    }

    private RateLimiterStrategy createStrategy(RuleConfig config) {
        switch (config.getStrategy()) {
            case FIXED_WINDOW:
                return new FixedWindowStrategy(config);
            case SLIDING_WINDOW_LOG:
                return new SlidingWindowLogStrategy(config);
            case TOKEN_BUCKET:
                return new TokenBucketStrategy(config);
            default:
                throw new IllegalArgumentException("Unknown strategy");
        }
    }

    public RateLimitResult evaluate(Request request, long now) {
        if (config.getScope() == Scope.GLOBAL) {
            return globalStrategy.evaluate(request.getCost(), now);
        } else {
            RateLimiterStrategy strategy = perKeyStrategies.computeIfAbsent(request.getKey(), k -> createStrategy(config));
            return strategy.evaluate(request.getCost(), now);
        }
    }

    public void cleanupStaleKeys() {
        perKeyStrategies.clear();
    }
}
