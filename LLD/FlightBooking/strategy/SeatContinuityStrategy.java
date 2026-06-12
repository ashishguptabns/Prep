package LLD.FlightBooking.strategy;

import java.util.List;

import LLD.FlightBooking.model.FlightSeatPool;
import LLD.FlightBooking.model.SeatClass;

public interface SeatContinuityStrategy {

    String resolveSeat(List<FlightSeatPool> seatPools, SeatClass seatClass, String preferredSeatId);
}
