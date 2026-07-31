package LLD.LiftSystem.component;

import LLD.LiftSystem.Lift;

public interface LiftComponent {
    void addTask(int floor);

    String getLiftName();

    Lift getLift();
}
