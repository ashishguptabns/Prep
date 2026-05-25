package LLD.SlotBookingApp.entity;

import LLD.SlotBookingApp.model.BookingStatus;
import java.util.UUID;

public class BookingEntity {
    private final String bookingId;
    private final String customerId;
    private final String slotId;
    private volatile BookingStatus status;

    public BookingEntity(String customerId, String slotId, BookingStatus status) {
        this.bookingId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.slotId = slotId;
        this.status = status;
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
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BookingEntity{bookingId='" + bookingId + "', customerId='" + customerId
                + "', slotId='" + slotId + "', status=" + status + "}";
    }
}
