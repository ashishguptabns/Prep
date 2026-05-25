package LLD.SlotBookingApp.strategy;

import LLD.SlotBookingApp.repository.WaitlistRepository;
import java.util.Optional;

public class FifoWaitlistPromotionStrategy implements WaitlistPromotionStrategy {
    private final WaitlistRepository waitlistRepository;

    public FifoWaitlistPromotionStrategy(WaitlistRepository waitlistRepository) {
        this.waitlistRepository = waitlistRepository;
    }

    @Override
    public Optional<String> nextBookingId(String slotId) {
        return waitlistRepository.poll(slotId);
    }
}
