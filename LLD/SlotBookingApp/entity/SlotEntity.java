package LLD.SlotBookingApp.entity;

import LLD.SlotBookingApp.model.WorkoutType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class SlotEntity {
    private final String slotId;
    private final String centerId;
    private final LocalDate date;
    private final LocalTime startsAt;
    private final LocalTime endsAt;
    private final WorkoutType workoutType;
    private final int capacity;

    public SlotEntity(String centerId, LocalDate date, LocalTime startsAt, LocalTime endsAt,
            WorkoutType workoutType, int capacity) {
        this.slotId = UUID.randomUUID().toString();
        this.centerId = centerId;
        this.date = date;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.workoutType = workoutType;
        this.capacity = capacity;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getCenterId() {
        return centerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartsAt() {
        return startsAt;
    }

    public LocalTime getEndsAt() {
        return endsAt;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public int getCapacity() {
        return capacity;
    }
}
