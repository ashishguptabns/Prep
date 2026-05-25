package LLD.SlotBookingApp.entity;

import java.time.LocalTime;
import java.util.UUID;

public class CenterEntity {
    private final String centerId;
    private final String name;
    private final LocalTime opensAt;
    private final LocalTime closesAt;

    public CenterEntity(String name, LocalTime opensAt, LocalTime closesAt) {
        this.centerId = UUID.randomUUID().toString();
        this.name = name;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    public String getCenterId() {
        return centerId;
    }

    public String getName() {
        return name;
    }

    public LocalTime getOpensAt() {
        return opensAt;
    }

    public LocalTime getClosesAt() {
        return closesAt;
    }
}
