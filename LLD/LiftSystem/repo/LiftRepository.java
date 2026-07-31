package LLD.LiftSystem.repo;

import java.util.ArrayList;
import java.util.List;

import LLD.LiftSystem.component.LiftComponent;

public class LiftRepository {

    private final List<LiftComponent> lifts = new ArrayList<>();

    public void add(LiftComponent lift) {
        lifts.add(lift);
    }

    public List<LiftComponent> getAll() {
        return lifts;
    }
}
