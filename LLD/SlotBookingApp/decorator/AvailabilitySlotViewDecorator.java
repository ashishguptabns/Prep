package LLD.SlotBookingApp.decorator;

import LLD.SlotBookingApp.model.SlotView;

public class AvailabilitySlotViewDecorator implements SlotViewDecorator {
    @Override
    public String decorate(SlotView slotView) {
        return slotView + ", availableSeats=" + slotView.getAvailableSeats();
    }
}
