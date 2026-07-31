package LLD.LiftSystem;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import LLD.LiftSystem.model.Direction;
import LLD.LiftSystem.model.LiftData;

public class Lift extends Thread {
    private final String liftName;
    private final AtomicReference<LiftData> liftState = new AtomicReference<>(
            new LiftData(0, Direction.IDLE, new PriorityQueue<>()));

    public Lift(String liftName, int numFloors) {
        this.liftName = liftName;
    }

    public String getLiftName() {
        return liftName;
    }

    public AtomicReference<LiftData> getLiftState() {
        return liftState;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            LiftData current = this.liftState.get();
            if (current.tasks().isEmpty()) {
                LockSupport.parkNanos(100_000_000L);
                continue;
            }
            int nextFloor = current.tasks().peek();
            if (current.currFloor() == nextFloor) {
                completeTask(nextFloor);
            } else {
                performStep(nextFloor);
            }

            LockSupport.parkNanos(300_000_000L);
        }
    }

    private void performStep(int targetFloor) {
        while (true) {
            LiftData current = liftState.get();
            int nextStep = current.currFloor() < targetFloor ? current.currFloor() + 1 : current.currFloor() - 1;
            Direction nextDir = current.currFloor() < targetFloor ? Direction.UP : Direction.DOWN;

            LiftData updated = new LiftData(nextStep, nextDir, new PriorityQueue<>(current.tasks()));

            if (liftState.compareAndSet(current, updated)) {
                System.out.printf("Lift %s moved to floor %d (Heading to %d)%n", liftName, nextStep, targetFloor);
                break;
            }
        }
    }

    private void completeTask(int floor) {
        while (true) {
            LiftData current = liftState.get();
            PriorityQueue<Integer> updatedTasks = new PriorityQueue<>(current.tasks());
            updatedTasks.poll();

            Direction nextDir = updatedTasks.isEmpty() ? Direction.IDLE : current.dir();
            LiftData updated = new LiftData(current.currFloor(), nextDir, updatedTasks);

            if (liftState.compareAndSet(current, updated)) {
                System.out.printf("Lift %s arrived at floor %d. Task completed.%n", liftName, floor);
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "Lift - " + liftName;
    }

    public void addTask(int toFloor) {
        while (true) {
            LiftData currState = this.liftState.get();
            PriorityQueue<Integer> newTasks = new PriorityQueue<>(currState.tasks());
            newTasks.add(toFloor);

            LiftData next = new LiftData(currState.currFloor(), currState.dir(), newTasks);
            if (this.liftState.compareAndSet(currState, next)) {
                System.out.println("Added task to lift - " + this.toString());
                break;
            }
        }
    }
}
