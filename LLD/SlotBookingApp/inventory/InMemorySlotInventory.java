package LLD.SlotBookingApp.inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemorySlotInventory implements SlotInventory {

    private final Map<String, AtomicInteger> confirmedCountsBySlotId = new ConcurrentHashMap<>();

    @Override
    public void registerSlot(String slotId) {
        confirmedCountsBySlotId.putIfAbsent(slotId, new AtomicInteger());
    }

    @Override
    public boolean tryReserve(String slotId, int capacity) {
        AtomicInteger confirmedCount = counterFor(slotId);
        while (true) {
            int current = confirmedCount.get();
            if (current >= capacity) {
                return false;
            }
            if (confirmedCount.compareAndSet(current, current + 1)) {
                return true;
            }
        }
    }

    @Override
    public void release(String slotId) {
        counterFor(slotId).updateAndGet(current -> Math.max(0, current - 1));
    }

    @Override
    public int confirmedCount(String slotId) {
        return counterFor(slotId).get();
    }

    private AtomicInteger counterFor(String slotId) {
        return confirmedCountsBySlotId.computeIfAbsent(slotId, id -> new AtomicInteger());
    }
}
