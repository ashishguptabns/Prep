package LLD.RateLimiterApp.strategy;

import java.util.concurrent.atomic.AtomicReference;

import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.RequestStatus;
import LLD.RateLimiterApp.model.RuleConfig;

class TokenBucketState {

    final double tokens;
    final long lastRefillTime;

    TokenBucketState(double tokens, long lastRefillTime) {
        this.tokens = tokens;
        this.lastRefillTime = lastRefillTime;
    }
}

public class TokenBucketStrategy implements RateLimiterStrategy {

    private final RuleConfig config;
    private final AtomicReference<TokenBucketState> stateRef;

    public TokenBucketStrategy(RuleConfig config) {
        this.config = config;
        this.stateRef = new AtomicReference<>(new TokenBucketState(config.getBucketCapacity(), System.currentTimeMillis()));
    }

    @Override
    public RateLimitResult evaluate(int cost, long now) {
        if (cost > config.getBucketCapacity()) {
            return new RateLimitResult(RequestStatus.THROTTLED, now, config.getRuleName(), 0, Long.MAX_VALUE);
        }

        while (true) {
            TokenBucketState current = stateRef.get();
            double elapsedTimeSec = (now - current.lastRefillTime) / 1000.0;
            double refilledTokens = current.tokens + (elapsedTimeSec * config.getRefillRatePerSec());
            double currentTokens = Math.min(config.getBucketCapacity(), refilledTokens);

            if (currentTokens >= cost) {
                double nextTokens = currentTokens - cost;
                if (stateRef.compareAndSet(current, new TokenBucketState(nextTokens, now))) {
                    return new RateLimitResult(RequestStatus.ALLOWED, now, null, (long) nextTokens, 0);
                }
            } else {
                double needed = cost - currentTokens;
                long retryAfter = (long) (Math.ceil(needed / config.getRefillRatePerSec()) * 1000);
                return new RateLimitResult(RequestStatus.THROTTLED, now, config.getRuleName(), (long) currentTokens, retryAfter);
            }
        }
    }
}
