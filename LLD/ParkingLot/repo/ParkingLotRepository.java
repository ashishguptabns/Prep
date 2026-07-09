package LLD.ParkingLot.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.ParkingLot.model.ParkingLot;

public class ParkingLotRepository {

    private final Map<String, ParkingLot> lotsById = new ConcurrentHashMap<>();

    public void save(ParkingLot lot) {
        lotsById.put(lot.getLotId(), lot);
    }

    public ParkingLot getLot(String lotId) {
        return lotsById.get(lotId);
    }
}
