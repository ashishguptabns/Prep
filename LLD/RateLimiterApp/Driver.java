package LLD.RateLimiterApp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import LLD.RateLimiterApp.model.RateLimitAlgorithm;
import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.RateLimitScope;
import LLD.RateLimiterApp.observer.MetricsObserver;
import LLD.RateLimiterApp.repository.RateLimitRuleRepository;
import LLD.RateLimiterApp.repository.RequestLogRepository;
import LLD.RateLimiterApp.service.RateLimiterService;

public class Driver {
    private static final String RESOURCE = "/api/orders";

    public static void main(String[] args) throws InterruptedException {
        scenario1BasicFixedWindow();
        scenario2RuleComposition();
        scenario3Concurrency();
        scenario4BlockingMode();
        scenario5WeightedRequests();
        scenario6MetricsDump();
    }

    private static void scenario1BasicFixedWindow() {
        System.out.println("\n1. Basic throttling - Fixed Window 5 req/10s PER_KEY");
        RateLimiterService service = newService("basic-fixed-window");
        service.createRule("user-1", RESOURCE, RateLimitAlgorithm.FIXED_WINDOW,
                RateLimitScope.PER_KEY, 5, 10_000, 5, 1, 1);

        for (int i = 1; i <= 7; i++) {
            print("request " + i, service.allowRequest("user-1", RESOURCE));
        }
        service.shutdown();
    }

    private static void scenario2RuleComposition() {
        System.out.println("\n2. Rule composition - GLOBAL fixed window + PER_KEY token bucket");
        RateLimiterService service = newService("composition");
        service.createRule("GLOBAL", RESOURCE, RateLimitAlgorithm.FIXED_WINDOW,
                RateLimitScope.GLOBAL, 10, 60_000, 10, 1, 1);
        for (String user : List.of("user-a", "user-b", "user-c", "user-d")) {
            service.createRule(user, RESOURCE, RateLimitAlgorithm.TOKEN_BUCKET,
                    RateLimitScope.PER_KEY, 3, 60_000, 3, 1, 1);
        }

        System.out.println("Per-key limit hits first for user-a:");
        for (int i = 1; i <= 4; i++) {
            print("user-a request " + i, service.allowRequest("user-a", RESOURCE));
        }

        System.out.println("Global limit hits when users spend the shared budget:");
        int requestNo = 1;
        for (String user : List.of("user-b", "user-c", "user-d")) {
            for (int i = 1; i <= 3; i++) {
                print(user + " request " + requestNo++, service.allowRequest(user, RESOURCE));
            }
        }
        service.shutdown();
    }

    private static void scenario3Concurrency() throws InterruptedException {
        System.out.println("\n3. Concurrency - 10 threads, same key, limit 5");
        RateLimiterService service = newService("concurrency");
        service.createRule("hot-user", RESOURCE, RateLimitAlgorithm.FIXED_WINDOW,
                RateLimitScope.PER_KEY, 5, 10_000, 5, 1, 1);

        int threadCount = 10;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger allowed = new AtomicInteger();
        List<RateLimitResult> results = new ArrayList<>();

        for (int i = 1; i <= threadCount; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    RateLimitResult result = service.allowRequest("hot-user", RESOURCE);
                    synchronized (results) {
                        results.add(result);
                    }
                    if (result.isAllowed()) {
                        allowed.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();
        executor.shutdown();

        for (int i = 0; i < results.size(); i++) {
            print("thread result " + (i + 1), results.get(i));
        }
        System.out.println("Allowed count=" + allowed.get() + " (must be <= 5)");
        service.shutdown();
    }

    private static void scenario4BlockingMode() {
        System.out.println("\n4. Blocking mode - Token Bucket cap=2 refill=1/s");
        RateLimiterService service = newService("blocking");
        service.createRule("blocking-user", RESOURCE, RateLimitAlgorithm.TOKEN_BUCKET,
                RateLimitScope.PER_KEY, 2, 60_000, 2, 1, 1);

        print("consume token 1", service.allowRequest("blocking-user", RESOURCE));
        print("consume token 2", service.allowRequest("blocking-user", RESOURCE));

        long allowedStart = System.currentTimeMillis();
        RateLimitResult eventuallyAllowed = service.allowRequestBlocking("blocking-user",
                RESOURCE, 1, 3_000);
        print("blocked then allowed after " + (System.currentTimeMillis() - allowedStart) + "ms",
                eventuallyAllowed);

        long timeoutStart = System.currentTimeMillis();
        RateLimitResult timedOut = service.allowRequestBlocking("blocking-user", RESOURCE, 2, 500);
        print("timed out after " + (System.currentTimeMillis() - timeoutStart) + "ms", timedOut);
        service.shutdown();
    }

    private static void scenario5WeightedRequests() {
        System.out.println("\n5. Weighted requests - costs 1, 5, 10 against capacity 8");
        RateLimiterService service = newService("weighted");
        service.createRule("weighted-user", RESOURCE, RateLimitAlgorithm.TOKEN_BUCKET,
                RateLimitScope.PER_KEY, 8, 60_000, 8, 1, 1);

        print("cost=1", service.allowRequest("weighted-user", RESOURCE, 1));
        print("cost=5", service.allowRequest("weighted-user", RESOURCE, 5));
        print("cost=10", service.allowRequest("weighted-user", RESOURCE, 10));
        service.shutdown();
    }

    private static void scenario6MetricsDump() throws InterruptedException {
        System.out.println("\n6. Metrics dump");
        RateLimiterService service = newService("metrics");
        MetricsObserver metricsObserver = new MetricsObserver();
        service.registerObserver(metricsObserver);
        service.createRule("metrics-user", RESOURCE, RateLimitAlgorithm.FIXED_WINDOW,
                RateLimitScope.PER_KEY, 2, 10_000, 2, 1, 1);

        for (int i = 1; i <= 4; i++) {
            print("metrics request " + i, service.allowRequest("metrics-user", RESOURCE));
        }

        Thread.sleep(100);
        System.out.print(metricsObserver.dump());
        service.shutdown();
    }

    private static RateLimiterService newService(String limiterName) {
        return new RateLimiterService(limiterName, new RateLimitRuleRepository(),
                new RequestLogRepository());
    }

    private static void print(String label, RateLimitResult result) {
        System.out.println("  " + label + " -> " + result);
    }
}
