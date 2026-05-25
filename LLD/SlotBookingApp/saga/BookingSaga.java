package LLD.SlotBookingApp.saga;

import java.util.ArrayDeque;
import java.util.Deque;

public class BookingSaga {
    private final Deque<Runnable> compensations = new ArrayDeque<>();
    private boolean completed;

    public void addCompensation(Runnable compensation) {
        compensations.push(compensation);
    }

    public void complete() {
        completed = true;
    }

    public void compensate() {
        if (completed) {
            return;
        }
        while (!compensations.isEmpty()) {
            compensations.pop().run();
        }
    }
}
