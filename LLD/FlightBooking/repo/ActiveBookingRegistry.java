package LLD.FlightBooking.repo;

public interface ActiveBookingRegistry {

    boolean reserve(String passengerId, String flightId, String bookingId);

    void release(String passengerId, String flightId, String bookingId);
}
