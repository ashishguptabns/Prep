package LLD.ParkingLot.saga;

import java.util.ArrayDeque;
import java.util.Deque;

public class BookingSaga {

    private final Deque<Runnable> compensations = new ArrayDeque<>();
    private boolean completed;

    public void addCompensation(Runnable compensation) {
        if (!completed) {
            compensations.push(compensation);
        }
    }

    public void complete() {
        completed = true;
        compensations.clear();
    }

    public void compensate() {
        while (!compensations.isEmpty()) {
            compensations.pop().run();
        }
    }
}
