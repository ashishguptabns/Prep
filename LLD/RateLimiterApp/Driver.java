package LLD.RateLimiterApp;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import LLD.RateLimiterApp.model.LimiterConfig;
import LLD.RateLimiterApp.model.LimiterMode;
import LLD.RateLimiterApp.model.Request;
import LLD.RateLimiterApp.model.RuleConfig;
import LLD.RateLimiterApp.model.Scope;
import LLD.RateLimiterApp.model.StrategyType;
import LLD.RateLimiterApp.observer.ConsoleObserver;
import LLD.RateLimiterApp.observer.MetricsObserver;
import LLD.RateLimiterApp.observer.RateLimitObserver;
import LLD.RateLimiterApp.service.RateLimiterService;

public class Driver {

    public static void main(String[] args) throws InterruptedException {
        first();
        second();
        third();
        fourth();
        fifth();
        sixth();
    }

    private static void first() throws InterruptedException {
        ConsoleObserver consoleObserver = new ConsoleObserver();
        MetricsObserver metricsObserver = new MetricsObserver();
        List<RateLimitObserver> observers = List.of(consoleObserver, metricsObserver);

        System.out.println("=== Scenario 1: Basic Throttling (Fixed Window: 5 req/10s) ===");
        RuleConfig s1Rule = new RuleConfig("fixed-5-10s", StrategyType.FIXED_WINDOW, Scope.PER_KEY, 5, 10, 0, 0);
        LimiterConfig s1Config = new LimiterConfig("s1-limiter", LimiterMode.NON_BLOCKING, 0, List.of(s1Rule), List.of("CONSOLE", "METRICS"));
        RateLimiterService s1Limiter = new RateLimiterService(s1Config, observers);

        for (int i = 0; i < 7; i++) {
            s1Limiter.check(new Request("user-A"));
        }

        Thread.sleep(500);
    }

    private static void second() throws InterruptedException {
        ConsoleObserver consoleObserver = new ConsoleObserver();
        MetricsObserver metricsObserver = new MetricsObserver();
        List<RateLimitObserver> observers = List.of(consoleObserver, metricsObserver);

        System.out.println("\n=== Scenario 2: Rule Composition (Global Fixed + Per-Key Token Bucket) ===");
        RuleConfig s2Global = new RuleConfig("global-max", StrategyType.FIXED_WINDOW, Scope.GLOBAL, 10, 60, 0, 0);
        RuleConfig s2PerKey = new RuleConfig("user-bucket", StrategyType.TOKEN_BUCKET, Scope.PER_KEY, 0, 0, 3, 1);
        LimiterConfig s2Config = new LimiterConfig("s2-limiter", LimiterMode.NON_BLOCKING, 0, List.of(s2Global, s2PerKey), List.of("CONSOLE", "METRICS"));
        RateLimiterService s2Limiter = new RateLimiterService(s2Config, observers);

        for (int i = 0; i < 5; i++) {
            s2Limiter.check(new Request("user-B"));
        }
        s2Limiter.check(new Request("user-C"));

        Thread.sleep(500);
    }

    private static void third() throws InterruptedException {
        ConsoleObserver consoleObserver = new ConsoleObserver();
        MetricsObserver metricsObserver = new MetricsObserver();
        List<RateLimitObserver> observers = List.of(consoleObserver, metricsObserver);
        System.out.println("\n=== Scenario 3: Concurrency Test (10 Threads targeting same key) ===");
        RuleConfig s3Rule = new RuleConfig("concurrent-fixed", StrategyType.FIXED_WINDOW, Scope.PER_KEY, 5, 10, 0, 0);
        LimiterConfig s3Config = new LimiterConfig("s3-limiter", LimiterMode.NON_BLOCKING, 0, List.of(s3Rule), List.of("CONSOLE", "METRICS"));
        RateLimiterService s3Limiter = new RateLimiterService(s3Config, observers);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> s3Limiter.check(new Request("user-concurrent")));
        }
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        Thread.sleep(500);
    }

    private static void fourth() throws InterruptedException {
        ConsoleObserver consoleObserver = new ConsoleObserver();
        MetricsObserver metricsObserver = new MetricsObserver();
        List<RateLimitObserver> observers = List.of(consoleObserver, metricsObserver);
        System.out.println("\n=== Scenario 4: Blocking Mode (Token Bucket Cap=2, Refill=1/s, Wait=3s) ===");
        RuleConfig s4Rule = new RuleConfig("blocking-bucket", StrategyType.TOKEN_BUCKET, Scope.PER_KEY, 0, 0, 2, 1);
        LimiterConfig s4Config = new LimiterConfig("s4-limiter", LimiterMode.BLOCKING, 3000, List.of(s4Rule), List.of("CONSOLE", "METRICS"));
        RateLimiterService s4Limiter = new RateLimiterService(s4Config, observers);

        s4Limiter.check(new Request("user-D"));
        s4Limiter.check(new Request("user-D"));

        System.out.println("[Driver] Sending 3rd request (Should block until token refills or timeout)...");
        s4Limiter.check(new Request("user-D"));

        System.out.println("[Driver] Sending 4th request with cost higher than capacity limit...");
        s4Limiter.check(new Request("user-D", 5));

        Thread.sleep(500);
    }

    private static void fifth() throws InterruptedException {
        ConsoleObserver consoleObserver = new ConsoleObserver();
        MetricsObserver metricsObserver = new MetricsObserver();
        List<RateLimitObserver> observers = List.of(consoleObserver, metricsObserver);
        System.out.println("\n=== Scenario 5: Weighted Requests ===");
        RuleConfig s5Rule = new RuleConfig("weighted-bucket", StrategyType.TOKEN_BUCKET, Scope.PER_KEY, 0, 0, 8, 1);
        LimiterConfig s5Config = new LimiterConfig("s5-limiter", LimiterMode.NON_BLOCKING, 0, List.of(s5Rule), List.of("CONSOLE", "METRICS"));
        RateLimiterService s5Limiter = new RateLimiterService(s5Config, observers);

        s5Limiter.check(new Request("user-E", 1));
        s5Limiter.check(new Request("user-E", 5));
        s5Limiter.check(new Request("user-E", 10));

        Thread.sleep(1000);

    }

    private static void sixth() throws InterruptedException {
        MetricsObserver metricsObserver = new MetricsObserver();
        System.out.println("\n=== Scenario 6: Metrics Dump ===");
        metricsObserver.dump();
    }
}
