package LLD.FlightBooking.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class FlightSeatPool {

    private final String flightId;
    private final Map<SeatClass, List<Seat>> seatsByClass;
    private final Set<String> reservedSeatIds;
    private final ReentrantLock lock = new ReentrantLock();

    public FlightSeatPool(String flightId, Map<SeatClass, Integer> initialSeats) {
        this.flightId = flightId;
        this.seatsByClass = new EnumMap<>(SeatClass.class);
        this.reservedSeatIds = new HashSet<>();
        for (Map.Entry<SeatClass, Integer> entry : initialSeats.entrySet()) {
            List<Seat> seats = new ArrayList<>();
            String prefix = entry.getKey() == SeatClass.BUSINESS ? "B" : "E";
            for (int i = 1; i <= entry.getValue(); i++) {
                seats.add(new Seat(prefix + i, entry.getKey()));
            }
            seatsByClass.put(entry.getKey(), seats);
        }
    }

    public String getFlightId() {
        return flightId;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public int getAvailable(SeatClass seatClass) {
        List<Seat> seats = seatsByClass.get(seatClass);
        if (seats == null) {
            return 0;
        }
        int available = 0;
        for (Seat seat : seats) {
            if (!reservedSeatIds.contains(seat.getSeatId())) {
                available++;
            }
        }
        return available;
    }

    public Set<String> getAvailableSeatIds(SeatClass seatClass) {
        Set<String> available = new HashSet<>();
        List<Seat> seats = seatsByClass.get(seatClass);
        if (seats == null) {
            return available;
        }
        for (Seat seat : seats) {
            if (!reservedSeatIds.contains(seat.getSeatId())) {
                available.add(seat.getSeatId());
            }
        }
        return available;
    }

    public boolean hasSeat(String seatId) {
        for (List<Seat> seats : seatsByClass.values()) {
            for (Seat seat : seats) {
                if (seat.getSeatId().equals(seatId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public SeatClass getSeatClass(String seatId) {
        for (Map.Entry<SeatClass, List<Seat>> entry : seatsByClass.entrySet()) {
            for (Seat seat : entry.getValue()) {
                if (seat.getSeatId().equals(seatId)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public List<String> tryReserve(SeatClass seatClass, int count) {
        List<String> assigned = new ArrayList<>();
        List<Seat> seats = seatsByClass.get(seatClass);
        if (seats == null) {
            return assigned;
        }
        for (Seat seat : seats) {
            if (assigned.size() == count) {
                break;
            }
            if (!reservedSeatIds.contains(seat.getSeatId()) && reservedSeatIds.add(seat.getSeatId())) {
                assigned.add(seat.getSeatId());
            }
        }
        if (assigned.size() < count) {
            for (String seatId : assigned) {
                reservedSeatIds.remove(seatId);
            }
            assigned.clear();
        }
        return assigned;
    }

    public boolean tryReserveSeat(String seatId) {
        if (!hasSeat(seatId) || reservedSeatIds.contains(seatId)) {
            return false;
        }
        return reservedSeatIds.add(seatId);
    }

    public void release(SeatClass seatClass, int count) {
        List<Seat> seats = seatsByClass.get(seatClass);
        if (seats == null) {
            return;
        }
        int released = 0;
        for (int i = seats.size() - 1; i >= 0 && released < count; i--) {
            String seatId = seats.get(i).getSeatId();
            if (reservedSeatIds.remove(seatId)) {
                released++;
            }
        }
    }

    public void releaseSeats(List<String> seatIds) {
        for (String seatId : seatIds) {
            reservedSeatIds.remove(seatId);
        }
    }

    public void releaseSeat(String seatId) {
        reservedSeatIds.remove(seatId);
    }
}
