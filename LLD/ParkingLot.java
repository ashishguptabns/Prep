package LLD;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

class ElectricChargingDecorator extends TicketDecorator {
    public ElectricChargingDecorator(ITicket ticket) {
        super(ticket);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Electric Charging included";
    }
}

class ValetDecorator extends TicketDecorator {
    public ValetDecorator(ITicket ticket) {
        super(ticket);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Valet Service";
    }
}

abstract class TicketDecorator implements ITicket {
    protected ITicket decoratedTicket;

    public TicketDecorator(ITicket ticket) {
        this.decoratedTicket = ticket;
    }

    @Override
    public String getDescription() {
        return decoratedTicket.getDescription();
    }

    @Override
    public String toString() {
        return getDescription();
    }

}

interface ITicket {
    String getDescription();
}

class Ticket implements ITicket {
    final Spot spot;

    public Ticket(Spot spot) {
        this.spot = spot;
    }

    @Override
    public String getDescription() {
        return "Ticket - Level - " + spot.getLevel() + " Num - " + spot.getNum();
    }

    @Override
    public String toString() {
        return getDescription();
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

class ClosestParkingStrategy implements ParkingStrategy {
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

public class ParkingLot {

    List<Level> levels = new ArrayList<>();
    final ParkingStrategy strategy;

    public ParkingLot(ParkingStrategy strategy) {
        this.strategy = strategy;
        for (int i = 0; i < 4; i++) {
            this.levels.add(new Level(i));
        }
    }

    ITicket issueTicket(boolean needsCharging, boolean valetNeeded) {
        Spot spot = this.strategy.findSpot(levels);
        if (spot != null) {
            if (spot.reserve()) {
                ITicket ticket = new Ticket(spot);
                if (needsCharging) {
                    ticket = new ElectricChargingDecorator(ticket);
                }
                if (valetNeeded) {
                    ticket = new ValetDecorator(ticket);
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
        ParkingLot app = new ParkingLot(new ClosestParkingStrategy());
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(30);
        for (int i = 0; i < 30; i++) {
            final int temp = i;
            executor.submit(() -> {
                try {
                    ITicket ticket = app.issueTicket(temp % 2 == 0, temp % 3 == 0);
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