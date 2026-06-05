package LLD.BNPLApp.inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryProductInventory implements ProductInventory {

    private final Map<String, AtomicInteger> stockByProductId = new ConcurrentHashMap<>();

    @Override
    public void registerProduct(String productId, int quantity) {
        stockByProductId.put(productId, new AtomicInteger(quantity));
    }

    @Override
    public boolean tryReserve(String productId, int quantity) {
        AtomicInteger stock = stockByProductId.get(productId);
        if (stock == null) {
            return false;
        }
        while (true) {
            int currentStock = stock.get();
            if (currentStock < quantity) {
                return false;
            }
            if (stock.compareAndSet(currentStock, currentStock - quantity)) {
                return true;
            }
        }
    }

    @Override
    public void release(String productId, int quantity) {
        stockByProductId.computeIfAbsent(productId, id -> new AtomicInteger(0)).addAndGet(quantity);
    }

    @Override
    public int availableQuantity(String productId) {
        AtomicInteger stock = stockByProductId.get(productId);
        return stock == null ? 0 : stock.get();
    }
}
