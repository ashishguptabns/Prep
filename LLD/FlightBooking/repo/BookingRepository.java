package LLD.FlightBooking.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.FlightBooking.model.Booking;

public class BookingRepository {

    private final Map<String, Booking> bookingsById = new ConcurrentHashMap<>();

    public void save(Booking booking) {
        bookingsById.put(booking.getBookingId(), booking);
    }

    public Booking getBooking(String bookingId) {
        return bookingsById.get(bookingId);
    }

    public void delete(String bookingId) {
        bookingsById.remove(bookingId);
    }
}
