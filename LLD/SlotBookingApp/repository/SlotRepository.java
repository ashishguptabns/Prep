package LLD.SlotBookingApp.repository;

import LLD.SlotBookingApp.entity.SlotEntity;
import LLD.SlotBookingApp.model.WorkoutType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SlotRepository {
    private final Map<String, SlotEntity> slots = new ConcurrentHashMap<>();

    public void save(SlotEntity slot) {
        slots.put(slot.getSlotId(), slot);
    }

    public Optional<SlotEntity> findById(String slotId) {
        return Optional.ofNullable(slots.get(slotId));
    }

    public List<SlotEntity> findByCenterAndWorkout(String centerId, WorkoutType workoutType) {
        List<SlotEntity> result = new ArrayList<>();
        for (SlotEntity slot : slots.values()) {
            if (slot.getCenterId().equals(centerId) && slot.getWorkoutType() == workoutType) {
                result.add(slot);
            }
        }
        return result;
    }
}
