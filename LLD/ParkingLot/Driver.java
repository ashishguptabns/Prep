package LLD.ParkingLot;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import LLD.ParkingLot.exception.ParkingLotException;
import LLD.ParkingLot.model.AddOn;
import LLD.ParkingLot.model.ParkingLot;
import LLD.ParkingLot.model.Ticket;
import LLD.ParkingLot.model.Vehicle;
import LLD.ParkingLot.repo.ParkingLotRepository;
import LLD.ParkingLot.repo.SpotInventoryRepository;
import LLD.ParkingLot.repo.TicketRepository;
import LLD.ParkingLot.service.ParkingLotService;

public class Driver {

    public static void main(String[] args) {
        Driver app = new Driver();
        app.run();
    }

    void run() {
        runNormal();
        runConcurrent();
        runDuplicateTicket();
    }

    private void runNormal() {
        System.out.println("Normal");

        ParkingLotRepository parkingLotRepository = new ParkingLotRepository();
        SpotInventoryRepository spotInventoryRepository = new SpotInventoryRepository();
        TicketRepository ticketRepository = new TicketRepository();
        ParkingLotService service = new ParkingLotService(
                parkingLotRepository, spotInventoryRepository, ticketRepository);

        ParkingLot lot = service.createParkingLot(4, 4);
        System.out.println("Created " + lot);

        service.printAvailability(lot.getLotId());

        Vehicle vehicle = new Vehicle("ABC-123");
        Ticket ticket = service.issueTicket(vehicle, lot.getLotId(),
                List.of(new AddOn("Electric"), new AddOn("Valet")));
        System.out.println("Ticket issued: " + ticket.getDescription());

        service.printAvailability(lot.getLotId());

        service.releaseTicket(ticket.getTicketId());
        System.out.println("Ticket released: " + ticket.getTicketId());

        service.printAvailability(lot.getLotId());
        System.out.println();
    }

    private void runConcurrent() {
        System.out.println("Concurrent");

        ParkingLotRepository parkingLotRepository = new ParkingLotRepository();
        SpotInventoryRepository spotInventoryRepository = new SpotInventoryRepository();
        TicketRepository ticketRepository = new TicketRepository();
        ParkingLotService service = new ParkingLotService(
                parkingLotRepository, spotInventoryRepository, ticketRepository);

        ParkingLot lot = service.createParkingLot(4, 4);
        int totalCapacity = lot.getTotalCapacity();
        int requestCount = 30;

        ExecutorService executor = Executors.newFixedThreadPool(10);
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger soldOutCounter = new AtomicInteger(0);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(requestCount);

        for (int i = 0; i < requestCount; i++) {
            final int vehicleNum = i;
            executor.execute(() -> {
                try {
                    startLatch.await();
                    Vehicle vehicle = new Vehicle("VEH-" + vehicleNum);
                    Ticket ticket = service.issueTicket(vehicle, lot.getLotId(),
                            List.of(new AddOn("Electric"), new AddOn("Valet")));
                    successCounter.incrementAndGet();
                    System.out.println(ticket.getDescription());
                } catch (ParkingLotException e) {
                    soldOutCounter.incrementAndGet();
                    System.out.println("Sold out");
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
            service.printAvailability(lot.getLotId());
            if (successCounter.get() == totalCapacity
                    && soldOutCounter.get() == requestCount - totalCapacity) {
                System.out.println("Pass");
            } else {
                System.out.println("Fail - success=" + successCounter.get()
                        + ", soldOut=" + soldOutCounter.get());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println();
    }

    private void runDuplicateTicket() {
        System.out.println("Duplicate ticket");

        ParkingLotRepository parkingLotRepository = new ParkingLotRepository();
        SpotInventoryRepository spotInventoryRepository = new SpotInventoryRepository();
        TicketRepository ticketRepository = new TicketRepository();
        ParkingLotService service = new ParkingLotService(
                parkingLotRepository, spotInventoryRepository, ticketRepository);

        ParkingLot lot = service.createParkingLot(4, 4);
        Vehicle vehicle = new Vehicle("DUP-001");

        service.issueTicket(vehicle, lot.getLotId(), List.of());
        try {
            service.issueTicket(vehicle, lot.getLotId(), List.of());
            System.out.println("Fail - duplicate ticket allowed");
        } catch (ParkingLotException e) {
            System.out.println("Pass - " + e.getMessage());
        }
    }
}
