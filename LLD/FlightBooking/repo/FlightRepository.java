package LLD.FlightBooking.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.FlightBooking.model.Flight;

public class FlightRepository {

    private final Map<String, Flight> flightsById = new ConcurrentHashMap<>();

    public void addFlight(Flight flight) {
        flightsById.put(flight.getFlightId(), flight);
    }

    public Flight getFlight(String flightId) {
        return flightsById.get(flightId);
    }

    public Map<String, Flight> getAllFlights() {
        return flightsById;
    }
}
