package LLD.FlightBooking.model;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ItineraryBooking {

    private final String bookingId;
    private final String passengerId;
    private final String itineraryId;
    private final SeatClass seatClass;
    private final String seatId;
    private final List<SeatAssignment> seatAssignments;
    private final long totalPrice;
    private final AtomicReference<BookingStatus> status;

    public ItineraryBooking(String passengerId, String itineraryId, SeatClass seatClass,
            String seatId, List<SeatAssignment> seatAssignments, long totalPrice) {
        this(UUID.randomUUID().toString(), passengerId, itineraryId, seatClass, seatId,
                seatAssignments, totalPrice);
    }

    public ItineraryBooking(String bookingId, String passengerId, String itineraryId,
            SeatClass seatClass, String seatId, List<SeatAssignment> seatAssignments,
            long totalPrice) {
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.itineraryId = itineraryId;
        this.seatClass = seatClass;
        this.seatId = seatId;
        this.seatAssignments = List.copyOf(seatAssignments);
        this.totalPrice = totalPrice;
        this.status = new AtomicReference<>(BookingStatus.CONFIRMED);
    }

    public ItineraryBooking withAssignments(List<SeatAssignment> seatAssignments) {
        return new ItineraryBooking(bookingId, passengerId, itineraryId, seatClass, seatId,
                seatAssignments, totalPrice);
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public String getSeatId() {
        return seatId;
    }

    public List<SeatAssignment> getSeatAssignments() {
        return seatAssignments;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public BookingStatus getStatus() {
        return status.get();
    }

    public boolean compareAndSetStatus(BookingStatus expected, BookingStatus updated) {
        return status.compareAndSet(expected, updated);
    }

    @Override
    public String toString() {
        return "ItineraryBooking{bookingId='" + bookingId + "', itineraryId='" + itineraryId
                + "', seatClass=" + seatClass + ", seatId='" + seatId + "', assignments="
                + seatAssignments + ", totalPrice=" + totalPrice + ", status=" + getStatus() + "}";
    }
}
