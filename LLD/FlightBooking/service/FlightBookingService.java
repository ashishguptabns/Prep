package LLD.FlightBooking.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import LLD.FlightBooking.exception.FlightBookingException;
import LLD.FlightBooking.model.Booking;
import LLD.FlightBooking.model.BookingStatus;
import LLD.FlightBooking.model.Flight;
import LLD.FlightBooking.model.FlightSeatPool;
import LLD.FlightBooking.model.Passenger;
import LLD.FlightBooking.model.SeatClass;
import LLD.FlightBooking.repo.ActiveBookingRegistry;
import LLD.FlightBooking.repo.BookingRepository;
import LLD.FlightBooking.repo.FlightRepository;
import LLD.FlightBooking.repo.InMemoryActiveBookingRegistry;
import LLD.FlightBooking.repo.SeatInventoryRepository;
import LLD.FlightBooking.saga.BookingSaga;
import LLD.FlightBooking.strategy.PricingStrategy;
import LLD.FlightBooking.strategy.StandardPricingStrategy;

public class FlightBookingService {

    private final FlightRepository flightRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final BookingRepository bookingRepository;
    private final ActiveBookingRegistry activeBookingRegistry;
    private final PricingStrategy pricingStrategy;

    public FlightBookingService(FlightRepository flightRepository,
            SeatInventoryRepository seatInventoryRepository,
            BookingRepository bookingRepository) {
        this(flightRepository, seatInventoryRepository, bookingRepository,
                new InMemoryActiveBookingRegistry(),
                new StandardPricingStrategy());
    }

    public FlightBookingService(FlightRepository flightRepository,
            SeatInventoryRepository seatInventoryRepository,
            BookingRepository bookingRepository,
            ActiveBookingRegistry activeBookingRegistry,
            PricingStrategy pricingStrategy) {
        this.flightRepository = flightRepository;
        this.seatInventoryRepository = seatInventoryRepository;
        this.bookingRepository = bookingRepository;
        this.activeBookingRegistry = activeBookingRegistry;
        this.pricingStrategy = pricingStrategy;
    }

    public Flight addFlight(String origin, String destination, String departureTime,
            Map<SeatClass, Integer> totalSeats, long basePrice) {
        validateRoute(origin, destination);
        if (basePrice <= 0) {
            throw new FlightBookingException("Base price must be positive");
        }
        if (totalSeats.isEmpty()) {
            throw new FlightBookingException("Flight must have at least one seat class");
        }
        for (Map.Entry<SeatClass, Integer> entry : totalSeats.entrySet()) {
            if (entry.getValue() <= 0) {
                throw new FlightBookingException("Seat count must be positive for " + entry.getKey());
            }
        }

        Flight flight = new Flight(origin, destination, departureTime, totalSeats, basePrice);
        flightRepository.addFlight(flight);
        seatInventoryRepository.registerFlight(flight.getFlightId(), totalSeats);
        return flight;
    }

    public List<Flight> searchFlights(String origin, String destination) {
        List<Flight> matches = new ArrayList<>();
        for (Flight flight : flightRepository.getAllFlights().values()) {
            if (flight.getOrigin().equalsIgnoreCase(origin)
                    && flight.getDestination().equalsIgnoreCase(destination)) {
                matches.add(flight);
            }
        }
        return matches;
    }

    public void printAvailableSeats(String flightId) {
        Flight flight = findFlight(flightId);
        FlightSeatPool seatPool = findSeatPool(flightId);
        Map<SeatClass, Integer> availability = new EnumMap<>(SeatClass.class);
        for (SeatClass seatClass : flight.getTotalSeats().keySet()) {
            availability.put(seatClass, seatPool.getAvailable(seatClass));
        }
        System.out.println("Flight " + flight.getOrigin() + " -> " + flight.getDestination()
                + " available seats: " + availability);
    }

    public Booking bookFlight(Passenger passenger, String flightId, SeatClass seatClass, int seatCount) {
        if (seatCount <= 0) {
            throw new FlightBookingException("Seat count must be positive");
        }

        Flight flight = findFlight(flightId);
        FlightSeatPool seatPool = findSeatPool(flightId);
        if (!flight.getTotalSeats().containsKey(seatClass)) {
            throw new FlightBookingException("Seat class " + seatClass + " is not offered on this flight");
        }

        long totalPrice = pricingStrategy.calculatePrice(flight, seatClass, seatCount);
        Booking booking = new Booking(passenger.getPassengerId(), flightId, seatClass, seatCount, totalPrice);
        BookingSaga saga = new BookingSaga();

        try {
            if (!activeBookingRegistry.reserve(passenger.getPassengerId(), flightId, booking.getBookingId())) {
                throw new FlightBookingException("Passenger already has an active booking for this flight");
            }
            saga.addCompensation(() -> activeBookingRegistry.release(
                    passenger.getPassengerId(), flightId, booking.getBookingId()));

            if (!seatPool.getLock().tryLock()) {
                throw new FlightBookingException("Flight " + flightId + " is busy, try again");
            }
            try {
                if (seatPool.getAvailable(seatClass) < seatCount) {
                    throw new FlightBookingException("Only " + seatPool.getAvailable(seatClass)
                            + " " + seatClass + " seats available");
                }
                if (!seatPool.tryReserve(seatClass, seatCount)) {
                    throw new FlightBookingException("Unable to reserve " + seatCount + " "
                            + seatClass + " seats");
                }
                saga.addCompensation(() -> seatPool.release(seatClass, seatCount));
            } finally {
                seatPool.getLock().unlock();
            }

            bookingRepository.save(booking);
            saga.addCompensation(() -> bookingRepository.delete(booking.getBookingId()));

            saga.complete();
            return booking;
        } catch (RuntimeException exception) {
            saga.compensate();
            throw exception;
        }
    }

    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if (booking == null) {
            throw new FlightBookingException("Booking not found: " + bookingId);
        }

        while (true) {
            BookingStatus status = booking.getStatus();
            if (status == BookingStatus.CANCELLED) {
                return;
            }
            if (booking.compareAndSetStatus(BookingStatus.CONFIRMED, BookingStatus.CANCELLED)) {
                FlightSeatPool seatPool = findSeatPool(booking.getFlightId());
                seatPool.release(booking.getSeatClass(), booking.getSeatCount());
                activeBookingRegistry.release(
                        booking.getPassengerId(), booking.getFlightId(), booking.getBookingId());
                return;
            }
        }
    }

    private Flight findFlight(String flightId) {
        Flight flight = flightRepository.getFlight(flightId);
        if (flight == null) {
            throw new FlightBookingException("Flight not found: " + flightId);
        }
        return flight;
    }

    private FlightSeatPool findSeatPool(String flightId) {
        FlightSeatPool seatPool = seatInventoryRepository.getSeatPool(flightId);
        if (seatPool == null) {
            throw new FlightBookingException("Seat inventory not found for flight: " + flightId);
        }
        return seatPool;
    }

    private void validateRoute(String origin, String destination) {
        if (origin == null || origin.isBlank()) {
            throw new FlightBookingException("Origin is required");
        }
        if (destination == null || destination.isBlank()) {
            throw new FlightBookingException("Destination is required");
        }
        if (origin.equalsIgnoreCase(destination)) {
            throw new FlightBookingException("Origin and destination must differ");
        }
    }
}
