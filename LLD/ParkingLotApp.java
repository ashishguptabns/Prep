package LLD;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        final PriorityQueue<Spot> pq;
        volatile boolean hasFreeSpots = true;
        Set<Spot> bookedSpots = new HashSet<>();

        public Level(int num) {
            this.num = num;
            pq = new PriorityQueue<>((a, b) -> a.name.compareTo(b.name));
            initFreeSpots(20);
        }

        synchronized void recordBookedSpot(Spot spot) {
            this.bookedSpots.add(spot);
        }

        private void initFreeSpots(int numSpots) {
            while (numSpots-- > 0) {
                pq.offer(new Spot("Spot - " + numSpots, this));
            }
        }

        void addSpot(Spot spot) {
            this.pq.offer(spot);
            hasFreeSpots = true;

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }

        Spot findFreeSpot() {
            Spot spot = pq.poll();
            if (pq.isEmpty()) {
                hasFreeSpots = false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }

            if (spot != null) {
                return spot;
            }
            System.out.println("No free spots at " + this + " left - " + pq.size());
            return null;
        }

        @Override
        public String toString() {
            return "Level - " + this.num;
        }
    }

    class Spot {
        volatile boolean isEmpty = true;
        Vehicle vehicle;
        final String name;
        final Level level;

        public Spot(String name, Level level) {
            this.name = name;
            this.level = level;
        }

        synchronized boolean book(Vehicle vehicle) {
            if (this.isEmpty) {
                this.isEmpty = false;
                this.vehicle = vehicle;
                this.level.recordBookedSpot(this);

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                }

                return true;
            }
            return false;
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
                if (!level.hasFreeSpots) {
                    continue;
                }
                synchronized (level) {
                    Spot spot = level.findFreeSpot();
                    if (spot != null) {
                        return spot;
                    }
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
            Spot spot = this.parkingStrategy.findFreeSpot(this.levels, vehicle);
            if (spot != null) {
                boolean booked = false;
                try {
                    synchronized (spot) {
                        if (spot.book(vehicle)) {
                            booked = true;
                            return new Ticket(vehicle, spot);
                        }
                    }
                } finally {
                    if (!booked) {
                        synchronized (spot.level) {
                            System.out.println("Adding Spot back " + spot);
                            spot.level.addSpot(spot);
                        }
                    }
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
        ExecutorService executor = Executors.newFixedThreadPool(3);
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