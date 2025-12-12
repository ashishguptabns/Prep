package LLD;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/* 
 * 
For each request (floor, direction):

    If elevator is already moving in the same direction and the request lies in its path → highest priority
    Cost = |floor – elevator.currentFloor| minus small bonus

    If elevator is idle → medium priority
    Cost = distance to requested floor

    If elevator is moving opposite direction → lowest priority
    Cost = large penalty + distance

Elevator Thread (1 per lift)
Responsible for:
    moving the elevator car (simulated movement)
    updating current floor
    reading sensors
    opening/closing doors
    sending status updates to dispatcher

Dispatcher Thread
Responsible for:
    reading button events from the queue
    choosing best elevator
    pushing tasks to elevator queues
 */
public class LiftSystem {
    class Lift implements Runnable {
        final String name;
        final int numFloors;
        int currFloor = 0;
        Direction dir = Direction.IDLE;
        PriorityQueue<Integer> tasks = new PriorityQueue<>();
        volatile boolean isRunning = true;
        final ReentrantLock lock = new ReentrantLock();

        public Lift(String name, int numFloors) {
            this.name = name;
            this.numFloors = numFloors;
        }

        @Override
        public void run() {
            while (isRunning && !Thread.currentThread().isInterrupted()) {
                Integer nextFloor = null;
                lock.lock();
                try {
                    nextFloor = tasks.poll();
                } finally {
                    lock.unlock();
                }
                try {
                    if (nextFloor == null) {
                        dir = Direction.IDLE;
                        Thread.sleep(200);
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        continue;
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                }
                move(nextFloor);
            }
        }

        void move(int toFloor) {
            if (toFloor > currFloor) {
                dir = Direction.UP;
            } else if (toFloor < currFloor) {
                dir = Direction.DOWN;
            }
            while (currFloor != toFloor && !Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println(String
                            .format("Lift %s is moving from %d to %d",
                                    toString(), currFloor, toFloor));
                    sleep(300);
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                }
                currFloor += (dir == Direction.DOWN) ? -1 : 1;
            }
            System.out.println("Lift is idle - " + toString());
            dir = Direction.IDLE;
        }

        LiftState getState() {
            lock.lock();
            try {
                return new LiftState(this.dir,
                        this.currFloor, new PriorityQueue<>(tasks));
            } finally {
                lock.unlock();
            }
        }

        @Override
        public String toString() {
            return "Lift - " + name;
        }

        void addTask(int toFloor) {
            lock.lock();
            try {
                System.out.println("Added task to lift - "
                        + this.toString());
                tasks.add(toFloor);
            } finally {
                lock.unlock();
            }
        }
    }

    final List<Lift> liftsArr = new ArrayList<>();

    public LiftSystem(int numLifts, int numFloors) {
        while (numLifts-- > 0) {
            Lift lift = new Lift(numLifts + "", numFloors);
            liftsArr.add(lift);
        }
    }

    enum Direction {
        UP, DOWN, IDLE
    }

    class Req {
        final int fromFloor;
        final Direction dir;

        public Req(int fromFloor, Direction dir) {
            this.fromFloor = fromFloor;
            this.dir = dir;
        }

        @Override
        public String toString() {
            return "Req - " + fromFloor + " Dir - " + dir.toString();
        }
    }

    class LiftState {
        final Direction dir;
        final int floor;
        final PriorityQueue<Integer> tasks;

        public LiftState(Direction dir, int floor, PriorityQueue<Integer> tasks) {
            this.dir = dir;
            this.floor = floor;
            this.tasks = tasks;
        }
    }

    class LiftController implements Runnable {
        final java.util.concurrent.BlockingQueue<Req> q = new LinkedBlockingQueue<>();
        volatile boolean isRunning = true;

        @Override
        public void run() {
            while (isRunning) {
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

        int findScore(LiftState state, Req req) {
            int baseDist = Math.abs(state.floor - req.fromFloor);
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
                LiftState state = lift.getState();
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
        ExecutorService liftExecutor = java.util.concurrent.Executors.newFixedThreadPool(2);
        ExecutorService dispatcherExecutor = java.util.concurrent.Executors.newSingleThreadExecutor();

        LiftController controller = new LiftController();
        dispatcherExecutor.submit(controller);

        for (Lift lift : liftsArr) {
            liftExecutor.submit(lift);
        }

        controller.submitHallReq(new Req(5, Direction.DOWN));
        controller.submitHallReq(new Req(3, Direction.UP));
        controller.submitHallReq(new Req(5, Direction.DOWN));

        Thread.sleep(1000);

        controller.submitHallReq(new Req(1, Direction.DOWN));
        controller.submitHallReq(new Req(5, Direction.UP));
        controller.submitHallReq(new Req(3, Direction.UP));

        Thread.sleep(1000);

        controller.submitHallReq(new Req(5, Direction.DOWN));
        controller.submitHallReq(new Req(1, Direction.UP));

        Thread.sleep(3000);

        System.out.println("Shutting down");
        for (Lift lift : liftsArr) {
            lift.isRunning = false;
        }
        controller.isRunning = false;

        liftExecutor.shutdownNow();
        dispatcherExecutor.shutdownNow();
        liftExecutor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS);
        dispatcherExecutor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS);
    }

    public static void main(String[] a) throws InterruptedException {
        LiftSystem app = new LiftSystem(5, 10);
        app.run();
    }
}
