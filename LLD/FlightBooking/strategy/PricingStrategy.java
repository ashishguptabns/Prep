package LLD.FlightBooking.strategy;

import LLD.FlightBooking.model.Flight;
import LLD.FlightBooking.model.SeatClass;

public interface PricingStrategy {

    long calculatePrice(Flight flight, SeatClass seatClass, int seatCount);
}
