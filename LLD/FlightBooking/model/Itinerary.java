package LLD.FlightBooking.model;

import java.util.List;
import java.util.UUID;

public class Itinerary {

    private final String itineraryId;
    private final List<String> flightIds;

    public Itinerary(List<String> flightIds) {
        if (flightIds == null || flightIds.isEmpty()) {
            throw new IllegalArgumentException("Itinerary must have at least one flight");
        }
        this.itineraryId = UUID.randomUUID().toString();
        this.flightIds = List.copyOf(flightIds);
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public List<String> getFlightIds() {
        return flightIds;
    }

    public int legCount() {
        return flightIds.size();
    }

    @Override
    public String toString() {
        return "Itinerary{itineraryId='" + itineraryId + "', legs=" + flightIds + "}";
    }
}
