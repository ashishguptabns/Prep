package LLD;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
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
        private final LinkedBlockingDeque<Spot> freeSpots = new LinkedBlockingDeque<>();
        private final Semaphore semaphore;

        public Level(int num) {
            this.num = num;
            semaphore = new Semaphore(20, true);
            initFreeSpots(20);
        }

        boolean hasFreeSpots() {
            return semaphore.availablePermits() > 0;
        }

        private void initFreeSpots(int numSpots) {
            while (numSpots-- > 0) {
                freeSpots.add(new Spot("Spot - " + numSpots, this));
            }
        }

        void releaseSpot(Spot spot, ParkingStrategy strategy) {
            if (spot != null) {
                spot.unBook();
                freeSpots.offer(spot);
                semaphore.release();
            }
        }

        Spot findFreeSpot() {
            if (semaphore.tryAcquire()) {
                try {
                    Spot spot = freeSpots.poll();
                    if (spot != null) {
                        return spot;
                    }
                    semaphore.release();
                } catch (Exception e) {
                    semaphore.release();
                }
            }

            System.out.println("No free spots at " + this + " left - " + semaphore.availablePermits());
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

        void unBook() {
            this.vehicle.set(null);
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
        Spot findFreeSpot(List<Level> levels, Vehicle vehicle);
    }

    class FastestFillParkingStrategy implements ParkingStrategy {
        public static String name = "FastestFillParkingStrategy";

        @Override
        public Spot findFreeSpot(List<Level> levels, Vehicle vehicle) {
            for (Level level : levels) {
                Spot spot = level.findFreeSpot();
                if (spot != null) {
                    return spot;
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
        }

        Ticket issueTicket(Vehicle vehicle) {
            Spot spot = this.parkingStrategy.findFreeSpot(levels, vehicle);
            if (spot != null) {
                if (spot.book(vehicle)) {
                    return new Ticket(vehicle, spot);
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
        ParkingStrategy parkingStrategy = new FastestFillParkingStrategy();
        gateMap.put("Gate 1", new Gate("Gate 1", parkingStrategy,
                List.of(this.levels.get(0), this.levels.get(2))));
        gateMap.put("Gate 2", new Gate("Gate 2", parkingStrategy,
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