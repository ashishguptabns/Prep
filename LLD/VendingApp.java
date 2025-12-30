package LLD;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class VendingApp {
    enum State {
        AVAILABLE, RESERVED, DISPENSED
    }

    static class Item {
        final String id;
        final AtomicReference<State> state = new AtomicReference<>(State.AVAILABLE);

        Item(String id) {
            this.id = id;
        }
    }

    static class Reservation implements Comparable<Reservation> {
        final String itemId;
        final long expiry;
        final long seq = seqGenerator.getAndIncrement();
        static final AtomicLong seqGenerator = new AtomicLong();

        Reservation(String itemId, long expiry) {
            this.itemId = itemId;
            this.expiry = expiry;
        }

        @Override
        public int compareTo(Reservation o) {
            int res = Long.compare(this.expiry, o.expiry);
            return res != 0 ? res : Long.compare(this.seq, o.seq);
        }
    }

    private final Map<String, Item> inventory = new ConcurrentHashMap<>();
    private final ConcurrentSkipListSet<Reservation> ttlSet = new ConcurrentSkipListSet<>();

    public void addItem(String id) {
        inventory.put(id, new Item(id));
    }

    public boolean reserve(String id) {
        Item item = inventory.get(id);
        if (item != null && item.state.get() == State.AVAILABLE) {
            if (item.state.compareAndSet(State.AVAILABLE, State.RESERVED)) {
                ttlSet.add(new Reservation(id, System.currentTimeMillis() + 2000)); // 2s TTL
                return true;
            }
        }
        return false;
    }

    public boolean confirmDispense(String id) {
        Item item = inventory.get(id);
        if (item != null && item.state.compareAndSet(State.RESERVED, State.DISPENSED)) {
            // Optimization: Use a dummy reservation with same ID/TS if you store refs,
            // or just let the Watchdog skip it since state is no longer RESERVED.
            System.out.println("Hardware: Item " + id + " dispensed successfully.");
            return true;
        }
        return false;
    }

    void stopWatchDog() {
        if (dog != null) {
            dog.shutdownNow();
        }
    }

    ScheduledExecutorService dog;

    public void startWatchdog() {
        dog = Executors.newSingleThreadScheduledExecutor();
        dog.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            while (!ttlSet.isEmpty() && ttlSet.first().expiry < now) {
                Reservation expired = ttlSet.pollFirst();
                Item item = inventory.get(expired.itemId);
                if (item.state.compareAndSet(State.RESERVED, State.AVAILABLE)) {
                    System.out.println("Watchdog: Reverted " + expired.itemId);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        VendingApp app = new VendingApp();
        app.addItem("Coke");
        app.startWatchdog();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(9);
        int count = 5;
        while (count-- > 0) {
            executor.submit(() -> {
                try {
                    System.out.println("Reserved: " + app.reserve("Coke")); // true
                } finally {
                    latch.countDown();
                }
            });
        }
        Thread.sleep(3000); // Wait for TTL
        count = 4;
        while (count-- > 0) {
            executor.submit(() -> {
                try {
                    System.out.println("Reserved after TTL: " + app.reserve("Coke")); // true
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdownNow();
        app.stopWatchDog();
    }
}