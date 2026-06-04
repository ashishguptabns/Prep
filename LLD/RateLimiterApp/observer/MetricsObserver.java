package LLD.RateLimiterApp.observer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.Request;

public class MetricsObserver implements RateLimitObserver {

    private static class MetricKey {

        final String key;
        final String rule;
        final String status;

        MetricKey(String key, String rule, String status) {
            this.key = key;
            this.rule = rule;
            this.status = status;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MetricKey)) {
                return false;
            }
            MetricKey that = (MetricKey) o;
            return key.equals(that.key) && rule.equals(that.rule) && status.equals(that.status);
        }

        @Override
        public int hashCode() {
            return 31 * (31 * key.hashCode() + rule.hashCode()) + status.hashCode();
        }
    }

    private final ConcurrentHashMap<MetricKey, AtomicLong> counts = new ConcurrentHashMap<>();

    @Override
    public void onDecision(String limiterName, Request request, RateLimitResult result) {
        String ruleName = result.getThrottledByRule() != null ? result.getThrottledByRule() : "ALL_PASSED";
        MetricKey mKey = new MetricKey(request.getKey(), ruleName, result.getStatus().name());
        counts.computeIfAbsent(mKey, k -> new AtomicLong(0)).incrementAndGet();
    }

    public void dump() {
        System.out.println("--- METRICS DUMP ---");
        counts.forEach((mKey, count)
                -> System.out.printf("Key: %s | Rule: %s | Status: %s -> Count: %d%n",
                        mKey.key, mKey.rule, mKey.status, count.get())
        );
        System.out.println("--------------------");
    }
}
