package LLD.RateLimiterApp;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.entity.RequestLogEntity;
import LLD.RateLimiterApp.model.RateLimitAlgorithm;
import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.RateLimitScope;
import LLD.RateLimiterApp.repository.RateLimitRuleRepository;
import LLD.RateLimiterApp.repository.RequestLogRepository;
import LLD.RateLimiterApp.service.RateLimiterService;
import LLD.RateLimiterApp.service.RateLimiterService.RateLimitSummary;

public class Driver {
    private static final String RESOURCE = "/api/orders";

    public static void main(String[] args) throws InterruptedException {
        RateLimiterService service = new RateLimiterService(
                new RateLimitRuleRepository(),
                new RequestLogRepository());

        RateLimitRuleEntity globalRule = service.createRule("GLOBAL", RESOURCE,
                RateLimitAlgorithm.FIXED_WINDOW, RateLimitScope.GLOBAL, 10, 60_000, 10, 1, 1);
        RateLimitRuleEntity userARule = createPerKeyRule(service, "JWT_A");
        RateLimitRuleEntity userBRule = createPerKeyRule(service, "JWT_B");
        RateLimitRuleEntity userCRule = createPerKeyRule(service, "JWT_C");
        RateLimitRuleEntity userDRule = createPerKeyRule(service, "JWT_D");

        System.out.println("Global rule created: " + globalRule);
        System.out.println("Per-key rules created:");
        System.out.println("  " + userARule);
        System.out.println("  " + userBRule);
        System.out.println("  " + userCRule);
        System.out.println("  " + userDRule);
        System.out.println();

        System.out.println("Single user burst: PER_KEY token bucket hits first");
        for (int i = 1; i <= 4; i++) {
            printResult("JWT_A", i, service.allowRequest("JWT_A", RESOURCE));
        }
        System.out.println();

        System.out.println("Many users: GLOBAL fixed window is exhausted collectively");
        runRequests(service, "JWT_B", 3);
        runRequests(service, "JWT_C", 3);
        runRequests(service, "JWT_D", 4);
        System.out.println();

        runConcurrentDemo(service);

        RateLimitSummary summary = service.getSummary("JWT_A", RESOURCE);
        System.out.println("Rate Limit Summary for JWT_A: " + summary);
        System.out.println();

        List<RequestLogEntity> logs = service.getRequestHistory("JWT_A", RESOURCE);
        System.out.println("First 10 JWT_A request logs:");
        for (int i = 0; i < Math.min(10, logs.size()); i++) {
            System.out.println("  " + logs.get(i));
        }
    }

    private static RateLimitRuleEntity createPerKeyRule(RateLimiterService service,
            String clientId) {
        return service.createRule(clientId, RESOURCE, RateLimitAlgorithm.TOKEN_BUCKET,
                RateLimitScope.PER_KEY, 3, 60_000, 3, 1, 1);
    }

    private static void runRequests(RateLimiterService service, String clientId, int count) {
        for (int i = 1; i <= count; i++) {
            printResult(clientId, i, service.allowRequest(clientId, RESOURCE));
        }
    }

    private static void printResult(String clientId, int requestId, RateLimitResult result) {
        System.out.println("[" + clientId + " request_" + requestId + "] " + result);
    }

    private static void runConcurrentDemo(RateLimiterService service) throws InterruptedException {
        int numRequests = 4;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numRequests);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= numRequests; i++) {
            final int requestId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    RateLimitResult result = service.allowRequest("JWT_A", RESOURCE);
                    printResult("JWT_A concurrent", requestId, result);
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
    }
}
