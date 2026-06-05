package LLD.FlashSaleApp.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import LLD.FlashSaleApp.entity.AllocationEntity;
import LLD.FlashSaleApp.entity.ProductEntity;
import LLD.FlashSaleApp.entity.SaleEntity;
import LLD.FlashSaleApp.exception.FlashSaleException;
import LLD.FlashSaleApp.model.AllocationResult;
import LLD.FlashSaleApp.model.SaleStatus;
import LLD.FlashSaleApp.repository.AllocationRepository;
import LLD.FlashSaleApp.repository.AllocationStore;
import LLD.FlashSaleApp.repository.ProductRepository;
import LLD.FlashSaleApp.repository.ProductStore;
import LLD.FlashSaleApp.saga.AllocationSaga;
import LLD.FlashSaleApp.strategy.AllocationStrategy;
import LLD.FlashSaleApp.strategy.FcfsAllocationStrategy;

public class FlashSaleService {

    private final ProductStore productStore;
    private final AllocationStore allocationStore;
    private final AllocationStrategy allocationStrategy;
    private final ConcurrentHashMap<String, SaleEntity> sales = new ConcurrentHashMap<>();

    public FlashSaleService() {
        this.productStore = new ProductRepository();
        this.allocationStore = new AllocationRepository();
        this.allocationStrategy = new FcfsAllocationStrategy();
    }

    public ProductEntity createProduct(String name, long price) {
        if (name == null || name.isBlank()) {
            throw new FlashSaleException("Product name is required");
        }
        if (price <= 0) {
            throw new FlashSaleException("Product price must be positive");
        }
        ProductEntity product = new ProductEntity(name, price);
        productStore.save(product);
        return product;
    }

    public SaleEntity startFlashSale(String productId, int totalQuantity, long startTime,
            long endTime) {
        if (totalQuantity <= 0) {
            throw new FlashSaleException("Total quantity must be positive");
        }
        if (endTime <= startTime) {
            throw new FlashSaleException("End time must be after start time");
        }

        SaleEntity sale = new SaleEntity(productId, totalQuantity, startTime, endTime,
                SaleStatus.LIVE);
        sales.put(sale.getSaleId(), sale);

        allocationStrategy.initializeSale(sale.getSaleId(), totalQuantity);
        return sale;
    }

    public AllocationResult attemptAllocation(String saleId, String userId,
            int requestedQuantity) {
        SaleEntity sale = findSale(saleId);

        if (sale.getStatus() != SaleStatus.LIVE) {
            return new AllocationResult(false, 0, "Sale is not live");
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime < sale.getStartTime() || currentTime > sale.getEndTime()) {
            return new AllocationResult(false, 0, "Sale time window has closed");
        }

        AllocationSaga saga = new AllocationSaga();

        try {
            AllocationResult result = allocationStrategy.allocate(saleId, userId, requestedQuantity);

            if (!result.isSuccess()) {
                return result;
            }

            saga.addCompensation(() -> allocationStrategy.release(saleId, result.getAllocatedQuantity()));

            AllocationEntity allocation = new AllocationEntity(saleId, userId,
                    result.getAllocatedQuantity(), currentTime);
            allocationStore.save(allocation);
            saga.addCompensation(() -> allocationStore.delete(allocation.getAllocationId()));

            saga.complete();
            return result;
        } catch (RuntimeException exception) {
            saga.compensate();
            throw exception;
        }
    }

    public List<AllocationEntity> getSaleAllocations(String saleId) {
        findSale(saleId);
        return allocationStore.findBySaleId(saleId);
    }

    public List<AllocationEntity> getUserAllocationHistory(String userId) {
        return allocationStore.findByUserId(userId);
    }

    public int getRemainingInventory(String saleId) {
        findSale(saleId);
        return allocationStrategy.getAvailableQuantity(saleId);
    }

    public SaleSummary getSaleSummary(String saleId) {
        SaleEntity sale = findSale(saleId);
        List<AllocationEntity> allocations = allocationStore.findBySaleId(saleId);

        int totalAllocated = 0;
        int uniqueUsers = 0;
        int remaining = allocationStrategy.getAvailableQuantity(saleId);

        for (AllocationEntity allocation : allocations) {
            totalAllocated += allocation.getQuantity();
        }
        uniqueUsers = (int) allocations.stream().map(AllocationEntity::getUserId).distinct()
                .count();

        return new SaleSummary(sale.getProductId(), sale.getTotalQuantity(), totalAllocated,
                remaining, uniqueUsers);
    }

    private SaleEntity findSale(String saleId) {
        return sales.getOrDefault(saleId,
                new SaleEntity("", 0, 0, 0, SaleStatus.CANCELLED)); // Dummy for testing
    }

    private ProductEntity findProduct(String productId) {
        return productStore.findById(productId)
                .orElseThrow(() -> new FlashSaleException("Product not found: " + productId));
    }

    public static class SaleSummary {

        public final String productId;
        public final int totalQuantity;
        public final int totalAllocated;
        public final int remainingQuantity;
        public final int uniqueUsers;

        public SaleSummary(String productId, int totalQuantity, int totalAllocated,
                int remainingQuantity, int uniqueUsers) {
            this.productId = productId;
            this.totalQuantity = totalQuantity;
            this.totalAllocated = totalAllocated;
            this.remainingQuantity = remainingQuantity;
            this.uniqueUsers = uniqueUsers;
        }

        @Override
        public String toString() {
            return "SaleSummary{productId='" + productId + "', totalQuantity=" + totalQuantity
                    + ", totalAllocated=" + totalAllocated + ", remainingQuantity="
                    + remainingQuantity + ", uniqueUsers=" + uniqueUsers + "}";
        }
    }
}
