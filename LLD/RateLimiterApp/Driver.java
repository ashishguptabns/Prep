package LLD.RateLimiterApp;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.entity.RequestLogEntity;
import LLD.RateLimiterApp.model.RateLimitAlgorithm;
import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.repository.RateLimitRuleRepository;
import LLD.RateLimiterApp.repository.RequestLogRepository;
import LLD.RateLimiterApp.service.RateLimiterService;
import LLD.RateLimiterApp.service.RateLimiterService.RateLimitSummary;

public class Driver {

    public static void main(String[] args) throws InterruptedException {
        RateLimiterService service = new RateLimiterService(
                new RateLimitRuleRepository(),
                new RequestLogRepository());

        RateLimitRuleEntity rule = service.createRule("JWT_A", "/api/orders",
                RateLimitAlgorithm.TOKEN_BUCKET, 5, 60_000, 5, 1, 1);
        System.out.println("Rate limit rule created: " + rule);
        System.out.println();

        int numRequests = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numRequests);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= numRequests; i++) {
            final int requestId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    RateLimitResult result = service.allowRequest("JWT_A", "/api/orders");
                    System.out.println("[request_" + requestId + "] " + result);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("\nConcurrent rate limit check completed in " + duration + "ms");
        System.out.println();

        RateLimitSummary summary = service.getSummary("JWT_A", "/api/orders");
        System.out.println("Rate Limit Summary: " + summary);
        System.out.println();

        List<RequestLogEntity> logs = service.getRequestHistory("JWT_A", "/api/orders");
        System.out.println("First 10 request logs:");
        for (int i = 0; i < Math.min(10, logs.size()); i++) {
            System.out.println("  " + logs.get(i));
        }
    }
}
