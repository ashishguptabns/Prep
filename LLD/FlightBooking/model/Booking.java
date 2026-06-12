package LLD.FlightBooking.model;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Booking {

    private final String bookingId;
    private final String passengerId;
    private final String flightId;
    private final SeatClass seatClass;
    private final int seatCount;
    private final List<String> seatIds;
    private final long totalPrice;
    private final AtomicReference<BookingStatus> status;

    public Booking(String passengerId, String flightId, SeatClass seatClass, int seatCount,
            List<String> seatIds, long totalPrice) {
        this(UUID.randomUUID().toString(), passengerId, flightId, seatClass, seatCount, seatIds,
                totalPrice);
    }

    public Booking(String bookingId, String passengerId, String flightId, SeatClass seatClass,
            int seatCount, List<String> seatIds, long totalPrice) {
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.seatClass = seatClass;
        this.seatCount = seatCount;
        this.seatIds = List.copyOf(seatIds);
        this.totalPrice = totalPrice;
        this.status = new AtomicReference<>(BookingStatus.CONFIRMED);
    }

    public Booking withSeatIds(List<String> seatIds) {
        return new Booking(bookingId, passengerId, flightId, seatClass, seatCount, seatIds,
                totalPrice);
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getFlightId() {
        return flightId;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public List<String> getSeatIds() {
        return seatIds;
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
        return "Booking{bookingId='" + bookingId + "', passengerId='" + passengerId
                + "', flightId='" + flightId + "', seatClass=" + seatClass + ", seatCount="
                + seatCount + ", seatIds=" + seatIds + ", totalPrice=" + totalPrice
                + ", status=" + getStatus() + "}";
    }
}
