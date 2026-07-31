package LLD.LiftSystem;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import LLD.LiftSystem.component.LiftComponent;
import LLD.LiftSystem.model.Req;
import LLD.LiftSystem.strategy.DispatchStrategy;

public class LiftController extends Thread {
    private final BlockingQueue<Req> q = new LinkedBlockingQueue<>();
    private final DispatchStrategy strategy;
    private final List<LiftComponent> lifts;

    public LiftController(List<LiftComponent> lifts, DispatchStrategy strategy) {
        this.lifts = lifts;
        this.strategy = strategy;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Req req = q.take();
                Lift bestLift = strategy.findBestLift(this.lifts, req);
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

    public void submitHallReq(Req req) {
        System.out.println("New req added - " + req.toString());
        q.offer(req);
    }
}
