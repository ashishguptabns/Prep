package LLD.RateLimiterApp;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import LLD.RateLimiterApp.entity.RuleEntity;
import LLD.RateLimiterApp.model.RateLimitAlgorithm;
import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.RateLimitScope;
import LLD.RateLimiterApp.repository.RequestLogRepository;
import LLD.RateLimiterApp.service.RateLimiterService;
import LLD.RateLimiterApp.strategy.SlidingWindowLog;

public class Driver {

    private static final String RESOURCE = "/api/orders";

    public static void main(String[] args) throws InterruptedException {
        scenario3Concurrency();
    }

    private static void scenario3Concurrency() throws InterruptedException {
        RateLimiterService service = new RateLimiterService(new SlidingWindowLog(), new RequestLogRepository());
        RuleEntity rule = new RuleEntity.RuleBuilder()
                .user("hot-user")
                .resource(RESOURCE)
                .algorithm(RateLimitAlgorithm.SLIDING_WINDOW_LOG)
                .scope(RateLimitScope.PER_KEY)
                .limit(4)
                .window(10_000)
                .capacity(5)
                .refillRate(1)
                .leakRate(1)
                .build();

        int threadCount = 10;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger allowed = new AtomicInteger();
        List<RateLimitResult> results = new CopyOnWriteArrayList<>();

        for (int i = 1; i <= threadCount; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    // RateLimitResult result1 = service.allowRequest("hot-user", RESOURCE, List.of(rule));
                    RateLimitResult result = service.allowRequestBlocking("hot-user", RESOURCE, 1_000, List.of(rule));
                    results.add(result);
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

    private static void print(String label, RateLimitResult result) {
        System.out.println("  " + label + " -> " + result);
    }
}
