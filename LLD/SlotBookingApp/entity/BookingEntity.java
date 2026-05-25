package LLD.SlotBookingApp.entity;

import LLD.SlotBookingApp.model.BookingStatus;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class BookingEntity {
    private final String bookingId;
    private final String customerId;
    private final String slotId;
    private final AtomicReference<BookingStatus> status;

    public BookingEntity(String customerId, String slotId, BookingStatus status) {
        this.bookingId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.slotId = slotId;
        this.status = new AtomicReference<>(status);
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getSlotId() {
        return slotId;
    }

    public BookingStatus getStatus() {
        return status.get();
    }

    public void setStatus(BookingStatus status) {
        this.status.set(status);
    }

    public boolean compareAndSetStatus(BookingStatus expectedStatus, BookingStatus newStatus) {
        return status.compareAndSet(expectedStatus, newStatus);
    }

    @Override
    public String toString() {
        return "BookingEntity{bookingId='" + bookingId + "', customerId='" + customerId
                + "', slotId='" + slotId + "', status=" + getStatus() + "}";
    }
}
