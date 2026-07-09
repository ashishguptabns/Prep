package LLD.ParkingLot.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {

    private final int levelNum;
    private final List<Spot> spots;

    public Level(int levelNum, int spotCount) {
        this.levelNum = levelNum;
        this.spots = new ArrayList<>();
        for (int i = 0; i < spotCount; i++) {
            spots.add(new Spot(i, levelNum));
        }
    }

    public int getLevelNum() {
        return levelNum;
    }

    public List<Spot> getSpots() {
        return Collections.unmodifiableList(spots);
    }

    public Spot findAvailableSpot() {
        for (Spot spot : spots) {
            if (spot.getState() == SpotState.AVAILABLE) {
                return spot;
            }
        }
        return null;
    }

    public int getAvailableCount() {
        int available = 0;
        for (Spot spot : spots) {
            if (spot.getState() == SpotState.AVAILABLE) {
                available++;
            }
        }
        return available;
    }
}
