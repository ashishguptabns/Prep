package LLD.ParkingLot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class LotSpotPool {

    private final String lotId;
    private final List<Level> levels;
    private final ReentrantLock lock = new ReentrantLock();

    public LotSpotPool(String lotId, int levelCount, int spotsPerLevel) {
        this.lotId = lotId;
        this.levels = new ArrayList<>();
        for (int i = 0; i < levelCount; i++) {
            levels.add(new Level(i, spotsPerLevel));
        }
    }

    public String getLotId() {
        return lotId;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public int getAvailableCount() {
        int available = 0;
        for (Level level : levels) {
            available += level.getAvailableCount();
        }
        return available;
    }

    public Spot findSpot(String spotId) {
        for (Level level : levels) {
            for (Spot spot : level.getSpots()) {
                if (spot.getSpotId().equals(spotId)) {
                    return spot;
                }
            }
        }
        return null;
    }
}
