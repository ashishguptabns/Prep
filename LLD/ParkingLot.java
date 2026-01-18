package LLD;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

class Ticket {
    final Spot spot;
    final List<AddOn> decorators = new ArrayList<>();

    public Ticket(Spot spot) {
        this.spot = spot;
    }

    String getDescription() {
        return "Ticket - Level - " + spot.getLevel() + " Num - " + spot.getNum();
    }

    @Override
    public String toString() {
        return getDescription();
    }

    void addDecoration(AddOn addOn) {
        this.decorators.add(addOn);
    }
}

class Spot {
    enum State {
        AVAILABLE, RESERVED
    };

    AtomicReference<Spot.State> state = new AtomicReference<>(Spot.State.AVAILABLE);

    final int num;
    final int levelNum;

    int getLevel() {
        return levelNum;
    }

    int getNum() {
        return num;
    }

    public Spot(int num, int levelNum) {
        this.num = num;
        this.levelNum = levelNum;
    }

    public boolean reserve() {
        return state.compareAndSet(Spot.State.AVAILABLE, Spot.State.RESERVED);
    }

    public boolean release() {
        return state.compareAndSet(Spot.State.RESERVED, Spot.State.AVAILABLE);
    }
}

class Level {
    private final ConcurrentLinkedQueue<Spot> spots = new ConcurrentLinkedQueue<>();

    public Level(int num) {
        for (int i = 0; i < 4; i++) {
            spots.offer(new Spot(i, num));
        }
    }

    Spot findSpot() {
        return spots.poll();
    }

    void addSpot(Spot spot) {
        this.spots.offer(spot);
    }
}

interface ParkingStrategy {
    public Spot findSpot(List<Level> levels);

    public void addSpot(List<Level> levels, Spot spot);
}

class FastestParkingStrategy implements ParkingStrategy {
    @Override
    public Spot findSpot(List<Level> levels) {
        for (Level level : levels) {
            Spot spot = level.findSpot();
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    @Override
    public void addSpot(List<Level> levels, Spot spot) {
        levels.get(spot.levelNum).addSpot(spot);
    }
}

record AddOn(String name) {
}

public class ParkingLot {

    List<Level> levels = new ArrayList<>();
    final ParkingStrategy strategy;

    public ParkingLot(ParkingStrategy strategy) {
        this.strategy = strategy;
        for (int i = 0; i < 4; i++) {
            this.levels.add(new Level(i));
        }
    }

    Ticket issueTicket(List<AddOn> decorators) {
        Spot spot = this.strategy.findSpot(levels);
        if (spot != null) {
            if (spot.reserve()) {
                Ticket ticket = new Ticket(spot);
                for (AddOn a : decorators) {
                    ticket.addDecoration(a);
                }
                return ticket;
            } else {
                if (spot.release()) {
                    this.strategy.addSpot(levels, spot);
                }
            }
        }
        return null;
    }

    public static void main(String[] a) {
        ParkingLot app = new ParkingLot(new FastestParkingStrategy());
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(30);
        for (int i = 0; i < 30; i++) {
            final int temp = i;
            executor.submit(() -> {
                try {
                    Ticket ticket = app.issueTicket(List.of(new AddOn("Electric"), new AddOn("Valet")));
                    if (ticket != null) {
                        System.out.println(ticket.toString());
                    } else {
                        System.out.println("Sold out");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (Exception e) {
        }
        executor.shutdown();
    }
}