package LLD.SlotBookingApp.strategy;

import java.util.Optional;

public interface WaitlistPromotionStrategy {
    Optional<String> nextBookingId(String slotId);
}
