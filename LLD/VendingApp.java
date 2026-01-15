package LLD;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class VendingApp {

    interface PricingStrategy {
        double findPrice(double basePrice);
    }

    class RegularPricing implements PricingStrategy {
        @Override
        public double findPrice(double basePrice) {
            return basePrice;
        }
    }

    class DiscountPricing implements PricingStrategy {
        @Override
        public double findPrice(double basePrice) {
            return 0.9 * basePrice;
        }
    }

    enum State {
        AVAILABLE, RESERVED, DISPENSED
    }

    interface Vendable {
        String getDesc();

        double getCost();
    }

    static class Item implements Vendable {
        final String id;
        final AtomicReference<State> state = new AtomicReference<>(State.AVAILABLE);

        Item(String id) {
            this.id = id;
        }

        @Override
        public String getDesc() {
            return null;
        }

        @Override
        public double getCost() {
            return 0.0;
        }
    }

    static abstract class ItemDecorator implements Vendable {

        Vendable item;

        public ItemDecorator(Vendable item) {
            this.item = item;
        }

    }

    static class GiftWrap extends ItemDecorator {

        public GiftWrap(Vendable item) {
            super(item);
        }

        @Override
        public String getDesc() {
            return item.getDesc();
        }

        @Override
        public double getCost() {
            return item.getCost() * 2.1;
        }

    }

    static class Reservation implements Delayed {
        final String itemId;
        final long expiry;
        final long seq = seqGenerator.getAndIncrement();
        static final AtomicLong seqGenerator = new AtomicLong();

        Reservation(String itemId, long expiry) {
            this.itemId = itemId;
            this.expiry = expiry;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expiry - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.expiry, ((Reservation) o).expiry);
        }
    }

    private final Map<String, Item> inventory = new ConcurrentHashMap<>();
    private final DelayQueue<Reservation> delayQ = new DelayQueue<>();

    public void addItem(String id) {
        inventory.put(id, new Item(id));
    }

    public boolean reserve(String id) {
        Item item = inventory.get(id);
        if (item != null && item.state.compareAndSet(State.AVAILABLE, State.RESERVED)) {
            delayQ.add(new Reservation(id, 2000)); // 2s TTL
            return true;
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
        dog.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // 2. take() blocks until a reservation is actually expired
                    Reservation expired = delayQ.take();
                    Item item = inventory.get(expired.itemId);

                    if (item != null && item.state.compareAndSet(State.RESERVED, State.AVAILABLE)) {
                        System.out.println("Watchdog: Item " + expired.itemId + " reverted to AVAILABLE.");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
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