package LLD.SlotBookingApp.inventory;

public interface SlotInventory {
    void registerSlot(String slotId);

    boolean tryReserve(String slotId, int capacity);

    void release(String slotId);

    int confirmedCount(String slotId);
}
