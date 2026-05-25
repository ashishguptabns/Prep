package LLD.SlotBookingApp.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryActiveBookingRegistry implements ActiveBookingRegistry {
    private final Map<String, String> bookingIdsByCustomerSlot = new ConcurrentHashMap<>();

    @Override
    public boolean reserve(String customerId, String slotId, String bookingId) {
        return bookingIdsByCustomerSlot.putIfAbsent(activeKey(customerId, slotId), bookingId) == null;
    }

    @Override
    public void release(String customerId, String slotId, String bookingId) {
        bookingIdsByCustomerSlot.remove(activeKey(customerId, slotId), bookingId);
    }

    private String activeKey(String customerId, String slotId) {
        return customerId + ":" + slotId;
    }
}
