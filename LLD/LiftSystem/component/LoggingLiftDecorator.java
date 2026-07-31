package LLD.LiftSystem.component;

import LLD.LiftSystem.Lift;

public class LoggingLiftDecorator implements LiftComponent {
    protected final Lift decoratedLift;

    public LoggingLiftDecorator(Lift lift) {
        this.decoratedLift = lift;
    }

    @Override
    public Lift getLift() {
        return this.decoratedLift;
    }

    @Override
    public void addTask(int floor) {
        System.out.printf("[AUDIT] Lift %s receiving floor request: %d%n",
                decoratedLift.getLiftName(), floor);
        decoratedLift.addTask(floor);
    }

    @Override
    public String getLiftName() {
        return decoratedLift.getLiftName();
    }
}
