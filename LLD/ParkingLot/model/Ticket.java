package LLD.ParkingLot.model;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Ticket {

    private final String ticketId;
    private final String vehicleId;
    private final String lotId;
    private final String spotId;
    private final int levelNum;
    private final int spotNum;
    private final List<AddOn> addOns;
    private final AtomicReference<BookingStatus> status;

    public Ticket(String vehicleId, String lotId, Spot spot, List<AddOn> addOns) {
        this(UUID.randomUUID().toString(), vehicleId, lotId, spot, addOns);
    }

    public Ticket(String ticketId, String vehicleId, String lotId, Spot spot, List<AddOn> addOns) {
        this.ticketId = ticketId;
        this.vehicleId = vehicleId;
        this.lotId = lotId;
        this.spotId = spot.getSpotId();
        this.levelNum = spot.getLevelNum();
        this.spotNum = spot.getSpotNum();
        this.addOns = List.copyOf(addOns);
        this.status = new AtomicReference<>(BookingStatus.CONFIRMED);
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getLotId() {
        return lotId;
    }

    public String getSpotId() {
        return spotId;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public int getSpotNum() {
        return spotNum;
    }

    public List<AddOn> getAddOns() {
        return addOns;
    }

    public BookingStatus getStatus() {
        return status.get();
    }

    public boolean compareAndSetStatus(BookingStatus expected, BookingStatus updated) {
        return status.compareAndSet(expected, updated);
    }

    public String getDescription() {
        StringBuilder description = new StringBuilder("Ticket - Level - ")
                .append(levelNum)
                .append(" Num - ")
                .append(spotNum);
        for (AddOn addOn : addOns) {
            description.append(" [").append(addOn.name()).append("]");
        }
        return description.toString();
    }

    @Override
    public String toString() {
        return "Ticket{ticketId='" + ticketId + "', vehicleId='" + vehicleId + "', lotId='"
                + lotId + "', spotId='" + spotId + "', addOns=" + addOns + ", status="
                + getStatus() + "}";
    }
}
