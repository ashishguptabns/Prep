package LLD.ParkingLot.strategy;

import java.util.List;

import LLD.ParkingLot.model.Level;
import LLD.ParkingLot.model.Spot;

public interface ParkingStrategy {

    Spot findSpot(List<Level> levels);
}
