package LLD.SlotBookingApp.repository;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WaitlistRepository {
    private final Map<String, Queue<String>> waitlistsBySlotId = new ConcurrentHashMap<>();

    public void add(String slotId, String bookingId) {
        waitlistsBySlotId.computeIfAbsent(slotId, id -> new ConcurrentLinkedQueue<>()).offer(bookingId);
    }

    public Optional<String> poll(String slotId) {
        Queue<String> waitlist = waitlistsBySlotId.get(slotId);
        if (waitlist == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(waitlist.poll());
    }

    public void remove(String slotId, String bookingId) {
        Queue<String> waitlist = waitlistsBySlotId.get(slotId);
        if (waitlist != null) {
            waitlist.remove(bookingId);
        }
    }

    public int count(String slotId) {
        Queue<String> waitlist = waitlistsBySlotId.get(slotId);
        return waitlist == null ? 0 : waitlist.size();
    }
}
