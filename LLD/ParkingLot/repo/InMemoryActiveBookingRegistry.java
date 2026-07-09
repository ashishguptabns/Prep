package LLD.ParkingLot.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryActiveBookingRegistry implements ActiveBookingRegistry {

    private final Map<String, String> ticketIdsByVehicleLot = new ConcurrentHashMap<>();

    @Override
    public boolean reserve(String vehicleId, String lotId, String ticketId) {
        return ticketIdsByVehicleLot.putIfAbsent(activeKey(vehicleId, lotId), ticketId) == null;
    }

    @Override
    public void release(String vehicleId, String lotId, String ticketId) {
        ticketIdsByVehicleLot.remove(activeKey(vehicleId, lotId), ticketId);
    }

    private String activeKey(String vehicleId, String lotId) {
        return vehicleId + ":" + lotId;
    }
}
