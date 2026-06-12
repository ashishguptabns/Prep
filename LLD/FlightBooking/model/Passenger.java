package LLD.FlightBooking.model;

import java.util.UUID;

public class Passenger {

    private final String passengerId;
    private final String name;

    public Passenger(String name) {
        this.passengerId = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Passenger{passengerId='" + passengerId + "', name='" + name + "'}";
    }
}
