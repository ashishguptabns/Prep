package LLD.BNPLApp.entity;

import java.util.UUID;

import LLD.BNPLApp.model.PurchaseStatus;

public class PurchaseEntity {
    private final String purchaseId;
    private final String userId;
    private final String productId;
    private final int quantity;
    private final long amount;
    private final PurchaseStatus status;

    public PurchaseEntity(String userId, String productId, int quantity, long amount, PurchaseStatus status) {
        this.purchaseId = UUID.randomUUID().toString();
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
        this.status = status;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getAmount() {
        return amount;
    }

    public PurchaseStatus getStatus() {
        return status;
    }
}
