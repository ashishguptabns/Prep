package LLD.FlashSaleApp.repository;

import java.util.List;

import LLD.FlashSaleApp.entity.AllocationEntity;

public interface AllocationStore {
    void save(AllocationEntity allocation);

    void delete(String allocationId);

    List<AllocationEntity> findBySaleId(String saleId);

    List<AllocationEntity> findByUserId(String userId);
}
