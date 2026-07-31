package LLD.LiftSystem.strategy;

import java.util.List;

import LLD.LiftSystem.Lift;
import LLD.LiftSystem.component.LiftComponent;
import LLD.LiftSystem.model.Direction;
import LLD.LiftSystem.model.LiftData;
import LLD.LiftSystem.model.Req;

public class ProximityStrategy implements DispatchStrategy {

    @Override
    public Lift findBestLift(List<LiftComponent> lifts, Req req) {
        Lift bestLift = null;
        int bestScore = Integer.MAX_VALUE;
        for (LiftComponent lift : lifts) {
            LiftData state = lift.getLift().getLiftState().get();
            int score = calculateScore(state, req);
            if (score < bestScore) {
                bestScore = score;
                bestLift = lift.getLift();
            }
        }
        return bestLift;
    }

    private int calculateScore(LiftData state, Req req) {
        int baseDist = Math.abs(state.currFloor() - req.fromFloor);
        int dirPenalty = (state.dir() != Direction.IDLE && state.dir() != req.dir) ? 10 : 0;
        int loadPenalty = state.tasks().size() * 2;
        return baseDist + dirPenalty + loadPenalty;
    }
}
