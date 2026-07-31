package LLD.LiftSystem.model;

import java.util.PriorityQueue;

public record LiftData(int currFloor, Direction dir, PriorityQueue<Integer> tasks) {
}
