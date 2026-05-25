package LLD.SlotBookingApp.repository;

public interface ActiveBookingRegistry {
    boolean reserve(String customerId, String slotId, String bookingId);

    void release(String customerId, String slotId, String bookingId);
}
