package LLD.RateLimiterApp.observer;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import LLD.RateLimiterApp.model.RateLimitResult;

public class MetricsObserver implements RateLimitObserver {
    private final Map<String, Counters> countersByKeyAndRule = new ConcurrentHashMap<>();

    @Override
    public void onDecision(String limiterName, String clientId, String resourcePath, int cost,
            RateLimitResult result) {
        String rule = result.getThrottledByRule() == null ? "ALL_RULES" : result.getThrottledByRule();
        String key = clientId + "::" + rule;
        Counters counters = countersByKeyAndRule.computeIfAbsent(key, ignored -> new Counters());
        if (result.isAllowed()) {
            counters.allowed.incrementAndGet();
        } else {
            counters.throttled.incrementAndGet();
        }
    }

    public String dump() {
        StringBuilder output = new StringBuilder("MetricsObserver.dump()\n");
        Map<String, Counters> sortedCounters = new TreeMap<>(countersByKeyAndRule);
        for (Map.Entry<String, Counters> entry : sortedCounters.entrySet()) {
            Counters counters = entry.getValue();
            output.append("  ").append(entry.getKey()).append(" allowed=")
                    .append(counters.allowed.get()).append(" throttled=")
                    .append(counters.throttled.get()).append('\n');
        }
        return output.toString();
    }

    private static class Counters {
        private final AtomicInteger allowed = new AtomicInteger();
        private final AtomicInteger throttled = new AtomicInteger();
    }
}
