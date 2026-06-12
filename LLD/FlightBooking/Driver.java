package LLD.FlightBooking;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import LLD.FlightBooking.exception.FlightBookingException;
import LLD.FlightBooking.model.Booking;
import LLD.FlightBooking.model.Flight;
import LLD.FlightBooking.model.Passenger;
import LLD.FlightBooking.model.SeatClass;
import LLD.FlightBooking.repo.BookingRepository;
import LLD.FlightBooking.repo.FlightRepository;
import LLD.FlightBooking.repo.SeatInventoryRepository;
import LLD.FlightBooking.service.FlightBookingService;

public class Driver {

    public static void main(String[] args) {
        Driver app = new Driver();
        app.run();
    }

    void run() {
        runNormal();
        runConcurrent();
        runDuplicateBooking();
    }

    private void runNormal() {
        System.out.println("Normal");

        FlightRepository flightRepository = new FlightRepository();
        SeatInventoryRepository seatInventoryRepository = new SeatInventoryRepository();
        BookingRepository bookingRepository = new BookingRepository();
        FlightBookingService service = new FlightBookingService(
                flightRepository, seatInventoryRepository, bookingRepository);

        Map<SeatClass, Integer> seats = new EnumMap<>(SeatClass.class);
        seats.put(SeatClass.ECONOMY, 100);
        seats.put(SeatClass.BUSINESS, 20);

        Flight flight = service.addFlight("NYC", "LAX", "2026-06-15 08:00", seats, 250);
        System.out.println("Added " + flight);

        List<Flight> searchResults = service.searchFlights("NYC", "LAX");
        System.out.println("Search results: " + searchResults.size() + " flight(s)");

        service.printAvailableSeats(flight.getFlightId());

        Passenger alice = new Passenger("Alice");
        Booking booking = service.bookFlight(alice, flight.getFlightId(), SeatClass.ECONOMY, 2);
        System.out.println("Booking confirmed: " + booking);

        service.printAvailableSeats(flight.getFlightId());

        service.cancelBooking(booking.getBookingId());
        System.out.println("Booking cancelled: " + booking.getBookingId());

        service.printAvailableSeats(flight.getFlightId());
        System.out.println();
    }

    private void runConcurrent() {
        System.out.println("Concurrent");

        FlightRepository flightRepository = new FlightRepository();
        SeatInventoryRepository seatInventoryRepository = new SeatInventoryRepository();
        BookingRepository bookingRepository = new BookingRepository();
        FlightBookingService service = new FlightBookingService(
                flightRepository, seatInventoryRepository, bookingRepository);

        Map<SeatClass, Integer> seats = new EnumMap<>(SeatClass.class);
        seats.put(SeatClass.ECONOMY, 1);

        Flight flight = service.addFlight("SFO", "SEA", "2026-06-20 14:00", seats, 180);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int passengerNum = i;
            executor.execute(() -> {
                try {
                    startLatch.await();
                    Passenger passenger = new Passenger("Passenger-" + passengerNum);
                    service.bookFlight(passenger, flight.getFlightId(), SeatClass.ECONOMY, 1);
                    successCounter.incrementAndGet();
                } catch (FlightBookingException e) {
                    failureCounter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        try {
            endLatch.await();
            executor.shutdown();
            service.printAvailableSeats(flight.getFlightId());
            if (successCounter.get() == 1 && failureCounter.get() == threadCount - 1) {
                System.out.println("Pass");
            } else {
                System.out.println("Fail - success=" + successCounter.get()
                        + ", failure=" + failureCounter.get());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println();
    }

    private void runDuplicateBooking() {
        System.out.println("Duplicate booking");

        FlightRepository flightRepository = new FlightRepository();
        SeatInventoryRepository seatInventoryRepository = new SeatInventoryRepository();
        BookingRepository bookingRepository = new BookingRepository();
        FlightBookingService service = new FlightBookingService(
                flightRepository, seatInventoryRepository, bookingRepository);

        Map<SeatClass, Integer> seats = new EnumMap<>(SeatClass.class);
        seats.put(SeatClass.ECONOMY, 10);

        Flight flight = service.addFlight("BOS", "MIA", "2026-07-01 10:00", seats, 200);
        Passenger bob = new Passenger("Bob");

        service.bookFlight(bob, flight.getFlightId(), SeatClass.ECONOMY, 1);
        try {
            service.bookFlight(bob, flight.getFlightId(), SeatClass.ECONOMY, 1);
            System.out.println("Fail - duplicate booking allowed");
        } catch (FlightBookingException e) {
            System.out.println("Pass - " + e.getMessage());
        }
    }
}
