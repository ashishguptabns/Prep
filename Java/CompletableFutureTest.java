package Java;

import java.util.concurrent.*;

public class CompletableFutureTest {
    public static void main(String[] args) throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task started");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Result";
        });
        CountDownLatch latch = new CountDownLatch(1);
        future.thenApply(result -> {
            System.out.println("Processing " + result);
            return result.length();
        }).thenAccept(length -> {
            System.out.println("Length is " + length);
            latch.countDown();
        });

        System.out.println("Main thread continues");

        latch.await();

        // Wait for all async tasks to complete
        future.get();
    }
}
