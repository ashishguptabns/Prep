package LLD.ParkingLot.repo;

public interface ActiveBookingRegistry {

    boolean reserve(String vehicleId, String lotId, String ticketId);

    void release(String vehicleId, String lotId, String ticketId);
}
