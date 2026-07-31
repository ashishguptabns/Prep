package LLD.LiftSystem.service;

import LLD.LiftSystem.Lift;
import LLD.LiftSystem.LiftController;
import LLD.LiftSystem.component.LiftComponent;
import LLD.LiftSystem.component.LoggingLiftDecorator;
import LLD.LiftSystem.model.Req;
import LLD.LiftSystem.repo.LiftRepository;
import LLD.LiftSystem.strategy.DispatchStrategy;

public class LiftSystemService {

    private final LiftRepository liftRepository;
    private final DispatchStrategy dispatchStrategy;
    private LiftController controller;

    public LiftSystemService(LiftRepository liftRepository, DispatchStrategy dispatchStrategy) {
        this.liftRepository = liftRepository;
        this.dispatchStrategy = dispatchStrategy;
    }

    public void initialize(int numLifts, int numFloors) {
        while (numLifts-- > 0) {
            Lift lift = new Lift(numLifts + "", numFloors);
            LiftComponent liftComp = new LoggingLiftDecorator(lift);
            liftRepository.add(liftComp);
            lift.start();
        }
    }

    public void startController() {
        controller = new LiftController(liftRepository.getAll(), dispatchStrategy);
        controller.start();
    }

    public void submitHallReq(Req req) {
        controller.submitHallReq(req);
    }

    public void shutdown() {
        if (controller != null) {
            controller.interrupt();
        }
        for (LiftComponent lift : liftRepository.getAll()) {
            lift.getLift().interrupt();
        }
    }
}
