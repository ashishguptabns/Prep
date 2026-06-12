package LLD.FlightBooking.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryActiveBookingRegistry implements ActiveBookingRegistry {

    private final Map<String, String> bookingIdsByPassengerFlight = new ConcurrentHashMap<>();

    @Override
    public boolean reserve(String passengerId, String flightId, String bookingId) {
        return bookingIdsByPassengerFlight.putIfAbsent(activeKey(passengerId, flightId), bookingId) == null;
    }

    @Override
    public void release(String passengerId, String flightId, String bookingId) {
        bookingIdsByPassengerFlight.remove(activeKey(passengerId, flightId), bookingId);
    }

    private String activeKey(String passengerId, String flightId) {
        return passengerId + ":" + flightId;
    }
}
