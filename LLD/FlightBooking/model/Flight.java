package LLD.FlightBooking.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class Flight {

    private final String flightId;
    private final String origin;
    private final String destination;
    private final String departureTime;
    private final Map<SeatClass, Integer> totalSeats;
    private final long basePrice;

    public Flight(String origin, String destination, String departureTime,
            Map<SeatClass, Integer> totalSeats, long basePrice) {
        this.flightId = UUID.randomUUID().toString();
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalSeats = new EnumMap<>(totalSeats);
        this.basePrice = basePrice;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public Map<SeatClass, Integer> getTotalSeats() {
        return totalSeats;
    }

    public long getBasePrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return "Flight{flightId='" + flightId + "', route=" + origin + "->" + destination
                + ", departure='" + departureTime + "', seats=" + totalSeats + "}";
    }
}
