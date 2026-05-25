package LLD.SlotBookingApp.repository;

import LLD.SlotBookingApp.entity.BookingEntity;
import LLD.SlotBookingApp.model.BookingStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BookingRepository {
    private final Map<String, BookingEntity> bookings = new ConcurrentHashMap<>();

    public void save(BookingEntity booking) {
        bookings.put(booking.getBookingId(), booking);
    }

    public void delete(String bookingId) {
        bookings.remove(bookingId);
    }

    public Optional<BookingEntity> findById(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    public List<BookingEntity> findBySlotAndStatus(String slotId, BookingStatus status) {
        List<BookingEntity> result = new ArrayList<>();
        for (BookingEntity booking : bookings.values()) {
            if (booking.getSlotId().equals(slotId) && booking.getStatus() == status) {
                result.add(booking);
            }
        }
        return result;
    }

    public Optional<BookingEntity> findActiveByCustomerAndSlot(String customerId, String slotId) {
        for (BookingEntity booking : bookings.values()) {
            if (booking.getCustomerId().equals(customerId)
                    && booking.getSlotId().equals(slotId)
                    && booking.getStatus() != BookingStatus.CANCELLED) {
                return Optional.of(booking);
            }
        }
        return Optional.empty();
    }
}
