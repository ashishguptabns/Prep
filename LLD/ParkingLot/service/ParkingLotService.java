package LLD.ParkingLot.service;

import java.util.List;
import java.util.UUID;

import LLD.ParkingLot.exception.ParkingLotException;
import LLD.ParkingLot.model.AddOn;
import LLD.ParkingLot.model.BookingStatus;
import LLD.ParkingLot.model.LotSpotPool;
import LLD.ParkingLot.model.ParkingLot;
import LLD.ParkingLot.model.Spot;
import LLD.ParkingLot.model.Ticket;
import LLD.ParkingLot.model.Vehicle;
import LLD.ParkingLot.repo.ActiveBookingRegistry;
import LLD.ParkingLot.repo.InMemoryActiveBookingRegistry;
import LLD.ParkingLot.repo.ParkingLotRepository;
import LLD.ParkingLot.repo.SpotInventoryRepository;
import LLD.ParkingLot.repo.TicketRepository;
import LLD.ParkingLot.saga.BookingSaga;
import LLD.ParkingLot.strategy.FastestParkingStrategy;
import LLD.ParkingLot.strategy.ParkingStrategy;

public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final SpotInventoryRepository spotInventoryRepository;
    private final TicketRepository ticketRepository;
    private final ActiveBookingRegistry activeBookingRegistry;
    private final ParkingStrategy parkingStrategy;

    public ParkingLotService(ParkingLotRepository parkingLotRepository,
            SpotInventoryRepository spotInventoryRepository,
            TicketRepository ticketRepository) {
        this(parkingLotRepository, spotInventoryRepository, ticketRepository,
                new InMemoryActiveBookingRegistry(), new FastestParkingStrategy());
    }

    public ParkingLotService(ParkingLotRepository parkingLotRepository,
            SpotInventoryRepository spotInventoryRepository,
            TicketRepository ticketRepository,
            ActiveBookingRegistry activeBookingRegistry,
            ParkingStrategy parkingStrategy) {
        this.parkingLotRepository = parkingLotRepository;
        this.spotInventoryRepository = spotInventoryRepository;
        this.ticketRepository = ticketRepository;
        this.activeBookingRegistry = activeBookingRegistry;
        this.parkingStrategy = parkingStrategy;
    }

    public ParkingLot createParkingLot(int levelCount, int spotsPerLevel) {
        if (levelCount <= 0) {
            throw new ParkingLotException("Level count must be positive");
        }
        if (spotsPerLevel <= 0) {
            throw new ParkingLotException("Spots per level must be positive");
        }

        ParkingLot lot = new ParkingLot(levelCount, spotsPerLevel);
        parkingLotRepository.save(lot);
        spotInventoryRepository.registerLot(lot);
        return lot;
    }

    public void printAvailability(String lotId) {
        ParkingLot lot = findLot(lotId);
        LotSpotPool spotPool = findSpotPool(lotId);
        System.out.println("Lot " + lot.getLotId() + " available spots: "
                + spotPool.getAvailableCount() + "/" + lot.getTotalCapacity());
    }

    public Ticket issueTicket(Vehicle vehicle, String lotId, List<AddOn> addOns) {
        if (vehicle == null) {
            throw new ParkingLotException("Vehicle is required");
        }
        findLot(lotId);
        LotSpotPool spotPool = findSpotPool(lotId);
        List<AddOn> requestedAddOns = addOns == null ? List.of() : List.copyOf(addOns);

        String ticketId = UUID.randomUUID().toString();
        BookingSaga saga = new BookingSaga();

        try {
            if (!activeBookingRegistry.reserve(vehicle.getVehicleId(), lotId, ticketId)) {
                throw new ParkingLotException("Vehicle already has an active ticket for this lot");
            }
            saga.addCompensation(() -> activeBookingRegistry.release(
                    vehicle.getVehicleId(), lotId, ticketId));

            Spot spot = reserveSpot(spotPool);
            saga.addCompensation(spot::release);

            Ticket ticket = new Ticket(ticketId, vehicle.getVehicleId(), lotId, spot,
                    requestedAddOns);
            ticketRepository.save(ticket);
            saga.addCompensation(() -> ticketRepository.delete(ticketId));

            saga.complete();
            return ticket;
        } catch (RuntimeException exception) {
            saga.compensate();
            throw exception;
        }
    }

    public void releaseTicket(String ticketId) {
        Ticket ticket = ticketRepository.getTicket(ticketId);
        if (ticket == null) {
            throw new ParkingLotException("Ticket not found: " + ticketId);
        }

        while (true) {
            BookingStatus status = ticket.getStatus();
            if (status == BookingStatus.CANCELLED) {
                return;
            }
            if (ticket.compareAndSetStatus(BookingStatus.CONFIRMED, BookingStatus.CANCELLED)) {
                LotSpotPool spotPool = findSpotPool(ticket.getLotId());
                Spot spot = spotPool.findSpot(ticket.getSpotId());
                if (spot != null) {
                    spot.release();
                }
                activeBookingRegistry.release(
                        ticket.getVehicleId(), ticket.getLotId(), ticket.getTicketId());
                return;
            }
        }
    }

    private Spot reserveSpot(LotSpotPool spotPool) {
        while (true) {
            Spot candidate = parkingStrategy.findSpot(spotPool.getLevels());
            if (candidate == null) {
                throw new ParkingLotException("No spots available");
            }
            if (candidate.tryReserve()) {
                return candidate;
            }
        }
    }

    private ParkingLot findLot(String lotId) {
        ParkingLot lot = parkingLotRepository.getLot(lotId);
        if (lot == null) {
            throw new ParkingLotException("Parking lot not found: " + lotId);
        }
        return lot;
    }

    private LotSpotPool findSpotPool(String lotId) {
        LotSpotPool spotPool = spotInventoryRepository.getSpotPool(lotId);
        if (spotPool == null) {
            throw new ParkingLotException("Spot inventory not found for lot: " + lotId);
        }
        return spotPool;
    }
}
