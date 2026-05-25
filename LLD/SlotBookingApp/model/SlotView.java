package LLD.SlotBookingApp.model;

import LLD.SlotBookingApp.entity.SlotEntity;

public class SlotView {
    private final SlotEntity slot;
    private final int confirmedCount;
    private final int waitlistCount;

    public SlotView(SlotEntity slot, int confirmedCount, int waitlistCount) {
        this.slot = slot;
        this.confirmedCount = confirmedCount;
        this.waitlistCount = waitlistCount;
    }

    @Override
    public String toString() {
        return "SlotView{slotId='" + slot.getSlotId() + "', centerId='" + slot.getCenterId()
                + "', date=" + slot.getDate() + ", time=" + slot.getStartsAt() + "-" + slot.getEndsAt()
                + ", workoutType=" + slot.getWorkoutType() + ", capacity=" + slot.getCapacity()
                + ", confirmedCount=" + confirmedCount + ", waitlistCount=" + waitlistCount + "}";
    }
}
