package LLD.LiftSystem.strategy;

import java.util.List;

import LLD.LiftSystem.Lift;
import LLD.LiftSystem.component.LiftComponent;
import LLD.LiftSystem.model.Req;

public interface DispatchStrategy {
    Lift findBestLift(List<LiftComponent> lifts, Req req);
}
