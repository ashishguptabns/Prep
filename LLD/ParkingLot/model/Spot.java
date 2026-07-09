package LLD.ParkingLot.model;

import java.util.concurrent.atomic.AtomicReference;

public class Spot {

    private final AtomicReference<SpotState> state = new AtomicReference<>(SpotState.AVAILABLE);
    private final int spotNum;
    private final int levelNum;

    public Spot(int spotNum, int levelNum) {
        this.spotNum = spotNum;
        this.levelNum = levelNum;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public int getSpotNum() {
        return spotNum;
    }

    public SpotState getState() {
        return state.get();
    }

    public boolean tryReserve() {
        return state.compareAndSet(SpotState.AVAILABLE, SpotState.RESERVED);
    }

    public boolean release() {
        return state.compareAndSet(SpotState.RESERVED, SpotState.AVAILABLE);
    }

    public String getSpotId() {
        return "L" + levelNum + "-S" + spotNum;
    }

    @Override
    public String toString() {
        return getSpotId();
    }
}
