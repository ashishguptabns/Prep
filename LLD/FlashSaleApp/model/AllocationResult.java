package LLD.FlashSaleApp.model;

public class AllocationResult {
    private final boolean success;
    private final int allocatedQuantity;
    private final String reason;

    public AllocationResult(boolean success, int allocatedQuantity, String reason) {
        this.success = success;
        this.allocatedQuantity = allocatedQuantity;
        this.reason = reason;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "AllocationResult{success=" + success + ", allocatedQuantity=" + allocatedQuantity
                + ", reason='" + reason + "'}";
    }
}
