package LLD.ParkingLot.model;

import java.util.UUID;

public class ParkingLot {

    private final String lotId;
    private final int levelCount;
    private final int spotsPerLevel;

    public ParkingLot(int levelCount, int spotsPerLevel) {
        this(UUID.randomUUID().toString(), levelCount, spotsPerLevel);
    }

    public ParkingLot(String lotId, int levelCount, int spotsPerLevel) {
        this.lotId = lotId;
        this.levelCount = levelCount;
        this.spotsPerLevel = spotsPerLevel;
    }

    public String getLotId() {
        return lotId;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public int getSpotsPerLevel() {
        return spotsPerLevel;
    }

    public int getTotalCapacity() {
        return levelCount * spotsPerLevel;
    }

    @Override
    public String toString() {
        return "ParkingLot{lotId='" + lotId + "', levelCount=" + levelCount
                + ", spotsPerLevel=" + spotsPerLevel + "}";
    }
}
