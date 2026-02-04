import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

enum Status {
    AVAILABLE, RESERVED
}

interface Item {
    String getDetails();

    int getSize();
}

class Package implements Item {
    public String getDetails() {
        return "Pkg";
    }

    public int getSize() {
        return 1;
    }
}

abstract class ItemDecorator implements Item {
    protected Item item;

    ItemDecorator(Item i) {
        this.item = i;
    }
}

class InsuranceDecorator extends ItemDecorator {
    InsuranceDecorator(Item i) {
        super(i);
    }

    public String getDetails() {
        return item.getDetails() + " + Insured";
    }

    public int getSize() {
        return item.getSize();
    }
}

class Locker {
    String id;
    int size = 10;
    AtomicReference<Status> status = new AtomicReference<>(Status.AVAILABLE);
    Item assignedItem;

    Locker(String id, int size) {
        this.id = id;
    }
}

interface LockerStrategy {
    Locker find(Collection<Locker> lockers, Item item);
}

class FitStrategy implements LockerStrategy {
    public Locker find(Collection<Locker> lockers, Item item) {
        return lockers.stream().filter(l -> l.size >= item.getSize() && l.status.get() == Status.AVAILABLE).findFirst()
                .orElse(null);
    }
}

public class LockerSystem {
    private final ConcurrentHashMap<String, Locker> registry = new ConcurrentHashMap<>();
    private final BlockingQueue<Item> waitQueue = new LinkedBlockingQueue<>();

    public void process(Item item, LockerStrategy strategy) {
        Locker target = strategy.find(registry.values(), item);
        if (target != null && target.status.compareAndSet(Status.AVAILABLE, Status.RESERVED)) {
            target.assignedItem = new InsuranceDecorator(item);
            System.out.println("Assigned " + target.id + " to " + target.assignedItem.getDetails());
        } else {
            waitQueue.offer(item);
            System.out.println("Locker Full. Item Queued.");
        }
    }

    public static void main(String[] args) {
        LockerSystem sys = new LockerSystem();
        sys.registry.put("L1", new Locker("L1", 1));
        sys.process(new Package(), new FitStrategy());
        sys.process(new Package(), new FitStrategy()); // Triggers queue
    }
}