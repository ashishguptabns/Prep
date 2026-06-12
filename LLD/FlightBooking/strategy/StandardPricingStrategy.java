package LLD.FlightBooking.strategy;

import LLD.FlightBooking.model.Flight;
import LLD.FlightBooking.model.SeatClass;

public class StandardPricingStrategy implements PricingStrategy {

    private static final int BUSINESS_MULTIPLIER = 2;

    @Override
    public long calculatePrice(Flight flight, SeatClass seatClass, int seatCount) {
        if (seatCount <= 0) {
            throw new IllegalArgumentException("Seat count must be positive");
        }
        long unitPrice = flight.getBasePrice();
        if (seatClass == SeatClass.BUSINESS) {
            unitPrice *= BUSINESS_MULTIPLIER;
        }
        return unitPrice * seatCount;
    }
}
