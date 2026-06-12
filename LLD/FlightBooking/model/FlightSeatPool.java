package LLD.FlightBooking.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class FlightSeatPool {

    private final String flightId;
    private final Map<SeatClass, AtomicInteger> availableSeats;
    private final ReentrantLock lock = new ReentrantLock();

    public FlightSeatPool(String flightId, Map<SeatClass, Integer> initialSeats) {
        this.flightId = flightId;
        this.availableSeats = new EnumMap<>(SeatClass.class);
        for (Map.Entry<SeatClass, Integer> entry : initialSeats.entrySet()) {
            availableSeats.put(entry.getKey(), new AtomicInteger(entry.getValue()));
        }
    }

    public String getFlightId() {
        return flightId;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public int getAvailable(SeatClass seatClass) {
        AtomicInteger seats = availableSeats.get(seatClass);
        return seats == null ? 0 : seats.get();
    }

    public boolean tryReserve(SeatClass seatClass, int count) {
        AtomicInteger seats = availableSeats.get(seatClass);
        if (seats == null) {
            return false;
        }
        while (true) {
            int current = seats.get();
            if (current < count) {
                return false;
            }
            if (seats.compareAndSet(current, current - count)) {
                return true;
            }
        }
    }

    public void release(SeatClass seatClass, int count) {
        AtomicInteger seats = availableSeats.get(seatClass);
        if (seats != null) {
            seats.addAndGet(count);
        }
    }
}
