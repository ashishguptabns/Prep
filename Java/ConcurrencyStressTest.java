package Java;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyStressTest {

    private static final int THREAD_COUNT = 50;
    private static final int INCREMENTS_PER_THREAD = 10000;

    public static void main(String[] args) throws InterruptedException {
        // Use an AtomicInteger to handle high-contention increments safely
        AtomicInteger counter = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        System.out.println("Starting high-contention stress test...");

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    // All threads wait here until start latch hits zero
                    startLatch.await();
                    for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                        counter.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Release all threads simultaneously
        startLatch.countDown();

        // Wait for all workers to finish
        endLatch.await();
        executor.shutdown();

        int expected = THREAD_COUNT * INCREMENTS_PER_THREAD;
        System.out.println("Test Complete.");
        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + counter.get());

        if (expected == counter.get()) {
            System.out.println("SUCCESS: Atomic integrity maintained.");
        } else {
            System.err.println("FAILURE: Race condition detected.");
        }
    }
}