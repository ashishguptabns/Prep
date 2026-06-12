package LLD.FlightBooking.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.FlightBooking.model.Itinerary;

public class ItineraryRepository {

    private final Map<String, Itinerary> itinerariesById = new ConcurrentHashMap<>();

    public void save(Itinerary itinerary) {
        itinerariesById.put(itinerary.getItineraryId(), itinerary);
    }

    public Itinerary getItinerary(String itineraryId) {
        return itinerariesById.get(itineraryId);
    }
}
