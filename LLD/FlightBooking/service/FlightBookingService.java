package LLD.FlightBooking.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import LLD.FlightBooking.exception.FlightBookingException;
import LLD.FlightBooking.model.Booking;
import LLD.FlightBooking.model.BookingStatus;
import LLD.FlightBooking.model.Flight;
import LLD.FlightBooking.model.FlightSeatPool;
import LLD.FlightBooking.model.Itinerary;
import LLD.FlightBooking.model.ItineraryBooking;
import LLD.FlightBooking.model.Passenger;
import LLD.FlightBooking.model.SeatAssignment;
import LLD.FlightBooking.model.SeatClass;
import LLD.FlightBooking.repo.ActiveBookingRegistry;
import LLD.FlightBooking.repo.BookingRepository;
import LLD.FlightBooking.repo.FlightRepository;
import LLD.FlightBooking.repo.InMemoryActiveBookingRegistry;
import LLD.FlightBooking.repo.ItineraryBookingRepository;
import LLD.FlightBooking.repo.ItineraryRepository;
import LLD.FlightBooking.repo.SeatInventoryRepository;
import LLD.FlightBooking.saga.BookingSaga;
import LLD.FlightBooking.strategy.PricingStrategy;
import LLD.FlightBooking.strategy.SeatContinuityStrategy;
import LLD.FlightBooking.strategy.StandardPricingStrategy;
import LLD.FlightBooking.strategy.StrictSameSeatStrategy;

public class FlightBookingService {

    private final FlightRepository flightRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final BookingRepository bookingRepository;
    private final ItineraryRepository itineraryRepository;
    private final ItineraryBookingRepository itineraryBookingRepository;
    private final ActiveBookingRegistry activeBookingRegistry;
    private final PricingStrategy pricingStrategy;
    private final SeatContinuityStrategy seatContinuityStrategy;

    public FlightBookingService(FlightRepository flightRepository,
            SeatInventoryRepository seatInventoryRepository,
            BookingRepository bookingRepository) {
        this(flightRepository, seatInventoryRepository, bookingRepository,
                new ItineraryRepository(), new ItineraryBookingRepository(),
                new InMemoryActiveBookingRegistry(),
                new StandardPricingStrategy(),
                new StrictSameSeatStrategy());
    }

    public FlightBookingService(FlightRepository flightRepository,
            SeatInventoryRepository seatInventoryRepository,
            BookingRepository bookingRepository,
            ItineraryRepository itineraryRepository,
            ItineraryBookingRepository itineraryBookingRepository,
            ActiveBookingRegistry activeBookingRegistry,
            PricingStrategy pricingStrategy,
            SeatContinuityStrategy seatContinuityStrategy) {
        this.flightRepository = flightRepository;
        this.seatInventoryRepository = seatInventoryRepository;
        this.bookingRepository = bookingRepository;
        this.itineraryRepository = itineraryRepository;
        this.itineraryBookingRepository = itineraryBookingRepository;
        this.activeBookingRegistry = activeBookingRegistry;
        this.pricingStrategy = pricingStrategy;
        this.seatContinuityStrategy = seatContinuityStrategy;
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

    public List<Itinerary> searchItineraries(String origin, String destination, int maxStops) {
        if (maxStops < 0) {
            throw new FlightBookingException("Max stops cannot be negative");
        }
        List<Itinerary> itineraries = new ArrayList<>();
        Deque<RouteState> queue = new ArrayDeque<>();

        for (Flight flight : flightRepository.getAllFlights().values()) {
            if (flight.getOrigin().equalsIgnoreCase(origin)) {
                List<String> path = List.of(flight.getFlightId());
                if (flight.getDestination().equalsIgnoreCase(destination)) {
                    itineraries.add(new Itinerary(path));
                } else if (maxStops > 0) {
                    queue.add(new RouteState(flight.getDestination(), path, 0));
                }
            }
        }

        while (!queue.isEmpty()) {
            RouteState state = queue.removeFirst();
            if (state.stopsUsed >= maxStops) {
                continue;
            }
            for (Flight flight : flightRepository.getAllFlights().values()) {
                if (!flight.getOrigin().equalsIgnoreCase(state.currentCity)) {
                    continue;
                }
                List<String> nextPath = new ArrayList<>(state.flightIds);
                nextPath.add(flight.getFlightId());
                if (flight.getDestination().equalsIgnoreCase(destination)) {
                    itineraries.add(new Itinerary(nextPath));
                } else if (state.stopsUsed + 1 < maxStops) {
                    queue.add(new RouteState(flight.getDestination(), nextPath, state.stopsUsed + 1));
                }
            }
        }
        return itineraries;
    }

    public Itinerary createItinerary(List<String> flightIds) {
        if (flightIds == null || flightIds.isEmpty()) {
            throw new FlightBookingException("Itinerary must include at least one flight");
        }

        Flight previousFlight = findFlight(flightIds.get(0));
        for (int i = 1; i < flightIds.size(); i++) {
            Flight nextFlight = findFlight(flightIds.get(i));
            if (!previousFlight.getDestination().equalsIgnoreCase(nextFlight.getOrigin())) {
                throw new FlightBookingException("Flight " + previousFlight.getFlightId()
                        + " does not connect to flight " + nextFlight.getFlightId());
            }
            previousFlight = nextFlight;
        }

        Itinerary itinerary = new Itinerary(flightIds);
        itineraryRepository.save(itinerary);
        return itinerary;
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
        return bookFlight(passenger, flightId, seatClass, seatCount, null);
    }

    public Booking bookFlight(Passenger passenger, String flightId, SeatClass seatClass, int seatCount,
            String preferredSeatId) {
        if (seatCount <= 0) {
            throw new FlightBookingException("Seat count must be positive");
        }

        Flight flight = findFlight(flightId);
        FlightSeatPool seatPool = findSeatPool(flightId);
        if (!flight.getTotalSeats().containsKey(seatClass)) {
            throw new FlightBookingException("Seat class " + seatClass + " is not offered on this flight");
        }

        long totalPrice = pricingStrategy.calculatePrice(flight, seatClass, seatCount);
        Booking pendingBooking = new Booking(passenger.getPassengerId(), flightId, seatClass,
                seatCount, List.of(), totalPrice);
        String bookingId = pendingBooking.getBookingId();
        BookingSaga saga = new BookingSaga();

        try {
            if (!activeBookingRegistry.reserve(passenger.getPassengerId(), flightId, bookingId)) {
                throw new FlightBookingException("Passenger already has an active booking for this flight");
            }
            saga.addCompensation(() -> activeBookingRegistry.release(
                    passenger.getPassengerId(), flightId, bookingId));

            List<String> assignedSeatIds = reserveSeatsOnLeg(seatPool, seatClass, seatCount,
                    preferredSeatId, saga);

            Booking booking = pendingBooking.withSeatIds(assignedSeatIds);
            bookingRepository.save(booking);
            saga.addCompensation(() -> bookingRepository.delete(bookingId));

            saga.complete();
            return booking;
        } catch (RuntimeException exception) {
            saga.compensate();
            throw exception;
        }
    }

    public ItineraryBooking bookItinerary(Passenger passenger, String itineraryId, SeatClass seatClass) {
        return bookItinerary(passenger, itineraryId, seatClass, null);
    }

    public ItineraryBooking bookItinerary(Passenger passenger, String itineraryId, SeatClass seatClass,
            String preferredSeatId) {
        Itinerary itinerary = findItinerary(itineraryId);
        List<FlightSeatPool> seatPools = new ArrayList<>();
        long totalPrice = 0;

        for (String flightId : itinerary.getFlightIds()) {
            Flight flight = findFlight(flightId);
            if (!flight.getTotalSeats().containsKey(seatClass)) {
                throw new FlightBookingException("Seat class " + seatClass
                        + " is not offered on flight " + flightId);
            }
            seatPools.add(findSeatPool(flightId));
            totalPrice += pricingStrategy.calculatePrice(flight, seatClass, 1);
        }

        String seatId = seatContinuityStrategy.resolveSeat(seatPools, seatClass, preferredSeatId);
        ItineraryBooking pendingBooking = new ItineraryBooking(passenger.getPassengerId(),
                itineraryId, seatClass, seatId, List.of(), totalPrice);
        String bookingId = pendingBooking.getBookingId();
        BookingSaga saga = new BookingSaga();

        try {
            if (!activeBookingRegistry.reserve(passenger.getPassengerId(), itineraryId, bookingId)) {
                throw new FlightBookingException(
                        "Passenger already has an active booking for this itinerary");
            }
            saga.addCompensation(() -> activeBookingRegistry.release(
                    passenger.getPassengerId(), itineraryId, bookingId));

            List<SeatAssignment> assignments = new ArrayList<>();
            for (FlightSeatPool seatPool : seatPools) {
                if (!seatPool.getLock().tryLock()) {
                    throw new FlightBookingException("Flight " + seatPool.getFlightId()
                            + " is busy, try again");
                }
                try {
                    if (!seatPool.tryReserveSeat(seatId)) {
                        throw new FlightBookingException("Unable to reserve seat " + seatId
                                + " on flight " + seatPool.getFlightId());
                    }
                    saga.addCompensation(() -> seatPool.releaseSeat(seatId));
                    assignments.add(new SeatAssignment(seatPool.getFlightId(), seatId));
                } finally {
                    seatPool.getLock().unlock();
                }
            }

            ItineraryBooking booking = pendingBooking.withAssignments(assignments);
            itineraryBookingRepository.save(booking);
            saga.addCompensation(() -> itineraryBookingRepository.delete(bookingId));

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
                if (!booking.getSeatIds().isEmpty()) {
                    seatPool.releaseSeats(booking.getSeatIds());
                } else {
                    seatPool.release(booking.getSeatClass(), booking.getSeatCount());
                }
                activeBookingRegistry.release(
                        booking.getPassengerId(), booking.getFlightId(), booking.getBookingId());
                return;
            }
        }
    }

    public void cancelItineraryBooking(String bookingId) {
        ItineraryBooking booking = itineraryBookingRepository.getBooking(bookingId);
        if (booking == null) {
            throw new FlightBookingException("Itinerary booking not found: " + bookingId);
        }

        while (true) {
            BookingStatus status = booking.getStatus();
            if (status == BookingStatus.CANCELLED) {
                return;
            }
            if (booking.compareAndSetStatus(BookingStatus.CONFIRMED, BookingStatus.CANCELLED)) {
                for (SeatAssignment assignment : booking.getSeatAssignments()) {
                    findSeatPool(assignment.getFlightId()).releaseSeat(assignment.getSeatId());
                }
                activeBookingRegistry.release(
                        booking.getPassengerId(), booking.getItineraryId(), booking.getBookingId());
                return;
            }
        }
    }

    private List<String> reserveSeatsOnLeg(FlightSeatPool seatPool, SeatClass seatClass, int seatCount,
            String preferredSeatId, BookingSaga saga) {
        if (!seatPool.getLock().tryLock()) {
            throw new FlightBookingException("Flight " + seatPool.getFlightId() + " is busy, try again");
        }
        try {
            List<String> assignedSeatIds = new ArrayList<>();
            if (preferredSeatId != null && !preferredSeatId.isBlank()) {
                if (seatCount != 1) {
                    throw new FlightBookingException(
                            "Preferred seat can only be used when booking one seat");
                }
                SeatClass preferredClass = seatPool.getSeatClass(preferredSeatId);
                if (preferredClass != seatClass) {
                    throw new FlightBookingException("Seat " + preferredSeatId + " is not in class "
                            + seatClass);
                }
                if (!seatPool.tryReserveSeat(preferredSeatId)) {
                    throw new FlightBookingException("Seat " + preferredSeatId + " is not available");
                }
                saga.addCompensation(() -> seatPool.releaseSeat(preferredSeatId));
                assignedSeatIds.add(preferredSeatId);
            } else {
                if (seatPool.getAvailable(seatClass) < seatCount) {
                    throw new FlightBookingException("Only " + seatPool.getAvailable(seatClass)
                            + " " + seatClass + " seats available");
                }
                assignedSeatIds = seatPool.tryReserve(seatClass, seatCount);
                if (assignedSeatIds.size() < seatCount) {
                    throw new FlightBookingException("Unable to reserve " + seatCount + " "
                            + seatClass + " seats");
                }
                List<String> seatsToRelease = List.copyOf(assignedSeatIds);
                saga.addCompensation(() -> seatPool.releaseSeats(seatsToRelease));
            }
            return assignedSeatIds;
        } finally {
            seatPool.getLock().unlock();
        }
    }

    private Flight findFlight(String flightId) {
        Flight flight = flightRepository.getFlight(flightId);
        if (flight == null) {
            throw new FlightBookingException("Flight not found: " + flightId);
        }
        return flight;
    }

    private Itinerary findItinerary(String itineraryId) {
        Itinerary itinerary = itineraryRepository.getItinerary(itineraryId);
        if (itinerary == null) {
            throw new FlightBookingException("Itinerary not found: " + itineraryId);
        }
        return itinerary;
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

    private static final class RouteState {
        private final String currentCity;
        private final List<String> flightIds;
        private final int stopsUsed;

        private RouteState(String currentCity, List<String> flightIds, int stopsUsed) {
            this.currentCity = currentCity;
            this.flightIds = flightIds;
            this.stopsUsed = stopsUsed;
        }
    }
}
