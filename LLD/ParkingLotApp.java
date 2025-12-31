package LLD;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* 
 * Multiple entry gates, levels and spots
 * 
 * Use WAL with blocking queue for recovery
 * -    dedicated thread to flush to disk
 * 
 * Write critical events synchronously
 * 
 * Use ConcurrentSkipListSet to store available spots
 * -    thread safe and O(log n) look up
 * 
 * Atomic Integer per level to track level capacity
 */
public class ParkingLotApp {

    class Ticket {
        final Vehicle vehicle;
        final Spot spot;

        public Ticket(Vehicle vehicle, Spot spot) {
            this.vehicle = vehicle;
            this.spot = spot;
        }

        @Override
        public String toString() {
            return "Ticket - " + vehicle + " at " + spot;
        }
    }

    class Level {
        final int num;
        private final AtomicInteger availableCount = new AtomicInteger(20);
        private final ConcurrentLinkedQueue<Spot> freeSpots = new ConcurrentLinkedQueue<>();

        public Level(int num) {
            this.num = num;
            initFreeSpots(20);
        }

        boolean hasFreeSpots() {
            return availableCount.get() > 0;
        }

        private void initFreeSpots(int numSpots) {
            while (numSpots-- > 0) {
                freeSpots.add(new Spot("Spot - " + numSpots, this));
            }
        }

        void addSpot(Spot spot, ParkingStrategy strategy) {
            spot.vehicle.set(null);
            int count = availableCount.incrementAndGet();
            freeSpots.add(spot);
            if (count == 1) {
                strategy.addLevel(this);
            }
        }

        Spot findFreeSpot() {
            while (availableCount.get() > 0) {
                int curr = availableCount.get();
                if (curr > 0 && availableCount.compareAndSet(curr, curr - 1)) {
                    Spot spot = freeSpots.poll();
                    if (spot == null) {
                        availableCount.incrementAndGet();
                        return null;
                    }
                    return spot;
                }
            }

            System.out.println("No free spots at " + this + " left - " + availableCount.get());
            return null;
        }

        @Override
        public String toString() {
            return "Level - " + this.num;
        }
    }

    class Spot {
        AtomicReference<Vehicle> vehicle = new AtomicReference<>();
        final String name;
        final Level level;

        public Spot(String name, Level level) {
            this.name = name;
            this.level = level;
        }

        boolean book(Vehicle vehicle) {
            return this.vehicle.compareAndSet(null, vehicle);
        }

        @Override
        public String toString() {
            return this.name + " " + level.toString();
        }
    }

    class Vehicle {
        final String name;

        public Vehicle(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    interface ParkingStrategy {
        void addLevels(List<Level> levels);

        void addLevel(Level level);

        Spot findFreeSpot(Vehicle vehicle);
    }

    class FastestFillParkingStrategy implements ParkingStrategy {
        public static String name = "FastestFillParkingStrategy";
        ConcurrentSkipListSet<Level> activeLevels = new ConcurrentSkipListSet<>(
                Comparator.comparingInt(level -> level.num));

        public void addLevel(Level level) {
            activeLevels.add(level);
        }

        public void addLevels(List<Level> levels) {
            activeLevels.addAll(levels);
        }

        @Override
        public Spot findFreeSpot(Vehicle vehicle) {
            for (Level level : activeLevels) {
                Spot spot = level.findFreeSpot();
                if (spot != null) {
                    return spot;
                }
                if (!level.hasFreeSpots()) {
                    activeLevels.remove(level);
                }
            }
            return null;
        }
    }

    class Gate {
        final List<Level> levels;
        final ParkingStrategy parkingStrategy;

        public Gate(String name, ParkingStrategy parkingStrategy, List<Level> levels) {
            this.levels = levels;
            this.parkingStrategy = parkingStrategy;
            this.parkingStrategy.addLevels(levels);
        }

        Ticket issueTicket(Vehicle vehicle) {
            Spot spot = this.parkingStrategy.findFreeSpot(vehicle);
            if (spot != null) {
                if (spot.book(vehicle)) {
                    return new Ticket(vehicle, spot);
                } else {
                    spot.level.addSpot(spot, parkingStrategy);
                }
            }

            System.out.println("No Spot Found for " + vehicle.toString());
            return null;
        }
    }

    final Map<String, Gate> gateMap;
    final List<Level> levels;

    public ParkingLotApp() {
        this.levels = List.of(
                new Level(1),
                new Level(2),
                new Level(3),
                new Level(4));
        gateMap = new HashMap<>();
        gateMap.put("Gate 1", new Gate("Gate 1", new FastestFillParkingStrategy(),
                List.of(this.levels.get(0), this.levels.get(2))));
        gateMap.put("Gate 2", new Gate("Gate 2", new FastestFillParkingStrategy(),
                List.of(this.levels.get(1), this.levels.get(2), this.levels.get(3))));
    }

    void run() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(50);
        int count = 0;
        while (count++ < 50) {
            final int temp = count;
            executor.submit(() -> {
                try {
                    Ticket ticket = gateMap.get("Gate " + String.valueOf(temp % 2 + 1))
                            .issueTicket(new Vehicle("Vehicle - " + temp));
                    if (ticket != null) {
                        System.out.println(ticket.toString());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (Exception e) {
        } finally {
            System.out.println("Shutting down");
            executor.shutdownNow();
        }
    }

    public static void main(String[] ar) {
        ParkingLotApp app = new ParkingLotApp();
        app.run();
    }
}