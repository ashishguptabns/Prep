package LLD.FlashSaleApp.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import LLD.FlashSaleApp.model.AllocationResult;

public class FcfsAllocationStrategy implements AllocationStrategy {
    private final Map<String, AtomicInteger> availableQuantityBySale = new ConcurrentHashMap<>();
    private final Map<String, Integer> totalQuantityBySale = new ConcurrentHashMap<>();

    public void initializeSale(String saleId, int totalQuantity) {
        availableQuantityBySale.putIfAbsent(saleId, new AtomicInteger(totalQuantity));
        totalQuantityBySale.putIfAbsent(saleId, totalQuantity);
    }

    @Override
    public AllocationResult allocate(String saleId, String userId, int requestedQuantity) {
        AtomicInteger available = availableQuantityBySale.get(saleId);
        if (available == null) {
            return new AllocationResult(false, 0, "Sale not found");
        }

        if (requestedQuantity <= 0) {
            return new AllocationResult(false, 0, "Requested quantity must be positive");
        }

        while (true) {
            int currentAvailable = available.get();
            if (currentAvailable <= 0) {
                return new AllocationResult(false, 0, "Insufficient stock");
            }

            int toAllocate = Math.min(requestedQuantity, currentAvailable);
            int nextAvailable = currentAvailable - toAllocate;

            if (available.compareAndSet(currentAvailable, nextAvailable)) {
                return new AllocationResult(true, toAllocate,
                        "Allocated " + toAllocate + " units for user " + userId);
            }
        }
    }

    @Override
    public int getAvailableQuantity(String saleId) {
        AtomicInteger available = availableQuantityBySale.get(saleId);
        return available == null ? 0 : available.get();
    }

    @Override
    public void release(String saleId, int quantity) {
        AtomicInteger available = availableQuantityBySale.get(saleId);
        if (available != null) {
            available.addAndGet(quantity);
        }
    }
}
