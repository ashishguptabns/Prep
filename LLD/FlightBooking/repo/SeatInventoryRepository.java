package LLD.FlightBooking.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.FlightBooking.model.FlightSeatPool;
import LLD.FlightBooking.model.SeatClass;

public class SeatInventoryRepository {

    private final Map<String, FlightSeatPool> seatPoolsByFlightId = new ConcurrentHashMap<>();

    public void registerFlight(String flightId, Map<SeatClass, Integer> initialSeats) {
        seatPoolsByFlightId.putIfAbsent(flightId, new FlightSeatPool(flightId, initialSeats));
    }

    public FlightSeatPool getSeatPool(String flightId) {
        return seatPoolsByFlightId.get(flightId);
    }
}
