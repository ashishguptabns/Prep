package LLD.FlightBooking.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.FlightBooking.model.ItineraryBooking;

public class ItineraryBookingRepository {

    private final Map<String, ItineraryBooking> bookingsById = new ConcurrentHashMap<>();

    public void save(ItineraryBooking booking) {
        bookingsById.put(booking.getBookingId(), booking);
    }

    public ItineraryBooking getBooking(String bookingId) {
        return bookingsById.get(bookingId);
    }

    public void delete(String bookingId) {
        bookingsById.remove(bookingId);
    }
}
