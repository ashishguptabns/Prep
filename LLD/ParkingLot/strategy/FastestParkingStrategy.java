package LLD.ParkingLot.strategy;

import java.util.List;

import LLD.ParkingLot.model.Level;
import LLD.ParkingLot.model.Spot;

public class FastestParkingStrategy implements ParkingStrategy {

    @Override
    public Spot findSpot(List<Level> levels) {
        for (Level level : levels) {
            Spot spot = level.findAvailableSpot();
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }
}
