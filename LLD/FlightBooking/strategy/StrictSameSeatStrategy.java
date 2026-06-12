package LLD.FlightBooking.strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import LLD.FlightBooking.exception.FlightBookingException;
import LLD.FlightBooking.model.FlightSeatPool;
import LLD.FlightBooking.model.SeatClass;

public class StrictSameSeatStrategy implements SeatContinuityStrategy {

    @Override
    public String resolveSeat(List<FlightSeatPool> seatPools, SeatClass seatClass,
            String preferredSeatId) {
        if (seatPools.isEmpty()) {
            throw new FlightBookingException("Itinerary has no legs to assign seats");
        }

        if (preferredSeatId != null && !preferredSeatId.isBlank()) {
            validateSameSeatAvailable(seatPools, seatClass, preferredSeatId);
            return preferredSeatId;
        }

        Set<String> commonSeats = null;
        for (FlightSeatPool seatPool : seatPools) {
            Set<String> available = seatPool.getAvailableSeatIds(seatClass);
            if (commonSeats == null) {
                commonSeats = new HashSet<>(available);
            } else {
                commonSeats.retainAll(available);
            }
        }

        if (commonSeats == null || commonSeats.isEmpty()) {
            throw new FlightBookingException("No " + seatClass
                    + " seat is available on every leg of this itinerary");
        }

        return new TreeSet<>(commonSeats).first();
    }

    private void validateSameSeatAvailable(List<FlightSeatPool> seatPools, SeatClass seatClass,
            String seatId) {
        List<String> missingLegs = new ArrayList<>();
        for (FlightSeatPool seatPool : seatPools) {
            SeatClass seatClassOnLeg = seatPool.getSeatClass(seatId);
            if (seatClassOnLeg != seatClass) {
                missingLegs.add(seatPool.getFlightId());
                continue;
            }
            if (!seatPool.getAvailableSeatIds(seatClass).contains(seatId)) {
                missingLegs.add(seatPool.getFlightId());
            }
        }
        if (!missingLegs.isEmpty()) {
            throw new FlightBookingException("Seat " + seatId + " is not available on leg(s): "
                    + missingLegs);
        }
    }
}
