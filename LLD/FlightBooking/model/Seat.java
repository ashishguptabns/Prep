package LLD.FlightBooking.model;

public class Seat {

    private final String seatId;
    private final SeatClass seatClass;

    public Seat(String seatId, SeatClass seatClass) {
        this.seatId = seatId;
        this.seatClass = seatClass;
    }

    public String getSeatId() {
        return seatId;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    @Override
    public String toString() {
        return seatId;
    }
}
