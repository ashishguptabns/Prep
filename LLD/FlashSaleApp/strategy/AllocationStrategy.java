package LLD.FlashSaleApp.strategy;

import LLD.FlashSaleApp.model.AllocationResult;

public interface AllocationStrategy {

    void initializeSale(String saleId, int totalQuantity);

    AllocationResult allocate(String saleId, String userId, int requestedQuantity);

    void release(String saleId, int quantity);

    int getAvailableQuantity(String saleId);
}
