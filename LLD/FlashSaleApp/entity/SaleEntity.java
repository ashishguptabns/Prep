package LLD.FlashSaleApp.entity;

import LLD.FlashSaleApp.model.SaleStatus;
import java.util.UUID;

public class SaleEntity {
    private final String saleId;
    private final String productId;
    private final int totalQuantity;
    private final long startTime;
    private final long endTime;
    private final SaleStatus status;

    public SaleEntity(String productId, int totalQuantity, long startTime, long endTime, SaleStatus status) {
        this.saleId = UUID.randomUUID().toString();
        this.productId = productId;
        this.totalQuantity = totalQuantity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getSaleId() {
        return saleId;
    }

    public String getProductId() {
        return productId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public SaleStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "SaleEntity{saleId='" + saleId + "', productId='" + productId
                + "', totalQuantity=" + totalQuantity + ", status=" + status + "}";
    }
}
