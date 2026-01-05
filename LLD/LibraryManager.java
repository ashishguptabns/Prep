package LLD;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class LibraryManager {
    private final Semaphore permits = new Semaphore(10); // 10 books available
    private final ConcurrentHashMap<String, Transaction> registry = new ConcurrentHashMap<>();
    private final ScheduledExecutorService watchdog = Executors.newScheduledThreadPool(1);

    enum State {
        RESERVED, COMPLETED, EXPIRED
    }

    class Transaction {
        final AtomicReference<State> state = new AtomicReference<>(State.RESERVED);
    }

    public void reserve(String txId, long ttlMs) {
        if (permits.tryAcquire()) {
            registry.put(txId, new Transaction());
            watchdog.schedule(() -> expire(txId), ttlMs, TimeUnit.MILLISECONDS);
        }
    }

    public void complete(String txId) {
        Transaction tx = registry.get(txId);
        if (tx != null && tx.state.compareAndSet(State.RESERVED, State.COMPLETED)) {
            // Success: Permit is "consumed" permanently or handled by business logic
            registry.remove(txId);
        }
    }

    private void expire(String txId) {
        Transaction tx = registry.get(txId);
        if (tx != null && tx.state.compareAndSet(State.RESERVED, State.EXPIRED)) {
            permits.release();
            registry.remove(txId);
            System.out.println("Watchdog reclaimed permit for: " + txId);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LibraryManager lib = new LibraryManager();
        String txId = "book_123";

        // 1. Reserve a book
        lib.reserve(txId, 100); // 100ms TTL

        // 2. Simulate Race Condition: Complete vs Expire
        CountDownLatch startLine = new CountDownLatch(1);

        Thread userThread = new Thread(() -> {
            try {
                startLine.await();
                lib.complete(txId);
            } catch (Exception ignored) {
            }
        });

        userThread.start();
        startLine.countDown(); // Release thread to race with the watchdog

        userThread.join();
        Thread.sleep(200); // Wait for watchdog to potentially fire

        System.out.println("Remaining permits: " + lib.permits.availablePermits());
    }
}