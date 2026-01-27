package LLD;

import LLD.LiftSystem.HallReq;
import LLD.LiftSystem.Lift;
import LLD.LiftSystem.Lift.LiftData;
import LLD.LiftSystem.LiftController;
import LLD.LiftSystem.Req;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class LiftSystem {
    class Lift extends Thread {
        final String name;
        final AtomicReference<LiftData> state = new AtomicReference<>(
                new LiftData(0, Direction.IDLE, new PriorityQueue<>()));

        record LiftData(int currFloor, Direction dir, PriorityQueue<Integer> tasks) {
        }

        public Lift(String name, int numFloors) {
            this.name = name;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                LiftData current = this.state.get();
                if (current.tasks.isEmpty()) {
                    LockSupport.parkNanos(100_000_000L); // 100ms
                    continue;
                }
                int nextFloor = current.tasks.peek();
                if (current.currFloor() == nextFloor) {
                    completeTask(nextFloor);
                } else {
                    performStep(nextFloor);
                }

                try {
                    Thread.sleep(300); // Simulate transit time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void performStep(int targetFloor) {
            while (true) {
                LiftData current = state.get();
                int nextStep = current.currFloor() < targetFloor ? current.currFloor() + 1 : current.currFloor() - 1;
                Direction nextDir = current.currFloor() < targetFloor ? Direction.UP : Direction.DOWN;

                LiftData updated = new LiftData(nextStep, nextDir, new PriorityQueue<>(current.tasks()));

                if (state.compareAndSet(current, updated)) {
                    System.out.printf("Lift %s moved to floor %d (Heading to %d)%n", name, nextStep, targetFloor);
                    break;
                }
            }
        }

        private void completeTask(int floor) {
            while (true) {
                LiftData current = state.get();
                PriorityQueue<Integer> updatedTasks = new PriorityQueue<>(current.tasks());
                updatedTasks.poll();

                Direction nextDir = updatedTasks.isEmpty() ? Direction.IDLE : current.dir();
                LiftData updated = new LiftData(current.currFloor(), nextDir, updatedTasks);

                if (state.compareAndSet(current, updated)) {
                    System.out.printf("Lift %s arrived at floor %d. Task completed.%n", name, floor);
                    break;
                }
            }
        }

        @Override
        public String toString() {
            return "Lift - " + name;
        }

        void addTask(int toFloor) {

            while (true) {
                LiftData currState = this.state.get();
                PriorityQueue<Integer> newTasks = new PriorityQueue<>(currState.tasks());
                newTasks.add(toFloor);

                LiftData next = new LiftData(currState.currFloor(), currState.dir(), newTasks);
                if (this.state.compareAndSet(currState, next)) {
                    System.out.println("Added task to lift - "
                            + this.toString());
                    break;
                }
            }
        }
    }

    final List<Lift> liftsArr = new ArrayList<>();

    public LiftSystem(int numLifts, int numFloors) {
        while (numLifts-- > 0) {
            Lift lift = new Lift(numLifts + "", numFloors);
            liftsArr.add(lift);
            lift.start();
        }
    }

    enum Direction {
        UP, DOWN, IDLE
    }

    abstract class Req {
        int toFloor;
        int fromFloor;
        Direction dir;
    }

    class HallReq extends Req {

        public HallReq(int fromFloor, Direction dir) {
            this.fromFloor = fromFloor;
            this.dir = dir;
        }

        public HallReq(int fromFloor, int toFloor) {
            this.fromFloor = fromFloor;
            this.toFloor = toFloor;
        }

        @Override
        public String toString() {
            return "Req - " + fromFloor + " Dir - " + dir.toString();
        }
    }

    public class LiftState {
        final Direction dir;
        final int floor;
        final PriorityQueue<Integer> tasks;

        public LiftState(Direction dir, int floor, PriorityQueue<Integer> tasks) {
            this.dir = dir;
            this.floor = floor;
            this.tasks = tasks;
        }
    }

    class LiftController extends Thread {
        final java.util.concurrent.BlockingQueue<Req> q = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Req req = q.take();
                    Lift bestLift = findBestLift(req);
                    if (bestLift != null) {
                        System.out.println("Found lift - " + bestLift.toString());
                        bestLift.addTask(req.fromFloor);
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                }
            }
        }

        int findScore(LiftData state, Req req) {
            int baseDist = Math.abs(state.currFloor() - req.fromFloor);
            int dirPenalty = 0;
            if (state.dir != Direction.IDLE && state.dir != req.dir) {
                dirPenalty = 10;
            }
            int loadPenalty = state.tasks.size() * 2;
            return baseDist + dirPenalty + loadPenalty;
        }

        Lift findBestLift(Req req) {
            Lift ans = null;
            int bestScore = Integer.MAX_VALUE;
            for (Lift lift : liftsArr) {
                LiftData state = lift.state.get();
                int score = findScore(state, req);
                if (score < bestScore) {
                    ans = lift;
                    bestScore = score;
                }
            }
            return ans;
        }

        void submitHallReq(Req req) {
            System.out.println("New req added - " + req.toString());
            q.offer(req);
        }
    }

    void run() throws InterruptedException {
        LiftController controller = new LiftController();
        controller.start();

        controller.submitHallReq(new HallReq(5, Direction.DOWN));
        controller.submitHallReq(new HallReq(3, Direction.UP));
        controller.submitHallReq(new HallReq(5, Direction.DOWN));

        Thread.sleep(1000);

        controller.submitHallReq(new HallReq(1, Direction.DOWN));
        controller.submitHallReq(new HallReq(5, Direction.UP));
        controller.submitHallReq(new HallReq(3, Direction.UP));

        Thread.sleep(1000);

        controller.submitHallReq(new HallReq(5, Direction.DOWN));
        controller.submitHallReq(new HallReq(1, Direction.UP));

        Thread.sleep(5000);

        System.out.println("Shutting down");
        controller.interrupt();
        for (Lift lift : liftsArr) {
            lift.interrupt();
        }
    }

    public static void main(String[] a) throws InterruptedException {
        LiftSystem app = new LiftSystem(5, 10);
        app.run();
    }
}
