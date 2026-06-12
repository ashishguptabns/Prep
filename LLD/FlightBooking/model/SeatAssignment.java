package LLD.FlightBooking.model;

public class SeatAssignment {

    private final String flightId;
    private final String seatId;

    public SeatAssignment(String flightId, String seatId) {
        this.flightId = flightId;
        this.seatId = seatId;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getSeatId() {
        return seatId;
    }

    @Override
    public String toString() {
        return flightId + ":" + seatId;
    }
}
