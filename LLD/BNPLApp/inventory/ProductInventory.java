package LLD.BNPLApp.inventory;

public interface ProductInventory {
    void registerProduct(String productId, int quantity);

    boolean tryReserve(String productId, int quantity);

    void release(String productId, int quantity);

    int availableQuantity(String productId);
}
