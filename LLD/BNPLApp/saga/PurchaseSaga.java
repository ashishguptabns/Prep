package LLD.BNPLApp.saga;

import java.util.Stack;

public class PurchaseSaga {

    private final Stack<Runnable> compensations = new Stack<>();
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
