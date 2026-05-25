package LLD.FlashSaleApp.entity;

import java.util.UUID;

public class AllocationEntity {
    private final String allocationId;
    private final String saleId;
    private final String userId;
    private final int quantity;
    private final long allocationTime;

    public AllocationEntity(String saleId, String userId, int quantity, long allocationTime) {
        this.allocationId = UUID.randomUUID().toString();
        this.saleId = saleId;
        this.userId = userId;
        this.quantity = quantity;
        this.allocationTime = allocationTime;
    }

    public String getAllocationId() {
        return allocationId;
    }

    public String getSaleId() {
        return saleId;
    }

    public String getUserId() {
        return userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getAllocationTime() {
        return allocationTime;
    }

    @Override
    public String toString() {
        return "AllocationEntity{allocationId='" + allocationId + "', saleId='" + saleId
                + "', userId='" + userId + "', quantity=" + quantity
                + ", allocationTime=" + allocationTime + "}";
    }
}
