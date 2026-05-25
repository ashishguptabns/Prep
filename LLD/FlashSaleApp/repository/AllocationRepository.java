package LLD.FlashSaleApp.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import LLD.FlashSaleApp.entity.AllocationEntity;

public class AllocationRepository implements AllocationStore {
    private final Map<String, AllocationEntity> allocations = new ConcurrentHashMap<>();

    @Override
    public void save(AllocationEntity allocation) {
        allocations.put(allocation.getAllocationId(), allocation);
    }

    @Override
    public void delete(String allocationId) {
        allocations.remove(allocationId);
    }

    @Override
    public List<AllocationEntity> findBySaleId(String saleId) {
        List<AllocationEntity> result = new ArrayList<>();
        for (AllocationEntity allocation : allocations.values()) {
            if (allocation.getSaleId().equals(saleId)) {
                result.add(allocation);
            }
        }
        return result;
    }

    @Override
    public List<AllocationEntity> findByUserId(String userId) {
        List<AllocationEntity> result = new ArrayList<>();
        for (AllocationEntity allocation : allocations.values()) {
            if (allocation.getUserId().equals(userId)) {
                result.add(allocation);
            }
        }
        return result;
    }
}
