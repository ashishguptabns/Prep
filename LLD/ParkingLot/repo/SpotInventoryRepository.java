package LLD.ParkingLot.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.ParkingLot.model.LotSpotPool;
import LLD.ParkingLot.model.ParkingLot;

public class SpotInventoryRepository {

    private final Map<String, LotSpotPool> spotPoolsByLotId = new ConcurrentHashMap<>();

    public void registerLot(ParkingLot lot) {
        spotPoolsByLotId.putIfAbsent(lot.getLotId(),
                new LotSpotPool(lot.getLotId(), lot.getLevelCount(), lot.getSpotsPerLevel()));
    }

    public LotSpotPool getSpotPool(String lotId) {
        return spotPoolsByLotId.get(lotId);
    }
}
