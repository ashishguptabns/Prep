package LLD.LiftSystem.model;

import java.util.PriorityQueue;

public class LiftState {
    public final Direction dir;
    public final int floor;
    public final PriorityQueue<Integer> tasks;

    public LiftState(Direction dir, int floor, PriorityQueue<Integer> tasks) {
        this.dir = dir;
        this.floor = floor;
        this.tasks = tasks;
    }
}
