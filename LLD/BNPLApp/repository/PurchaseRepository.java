package LLD.BNPLApp.repository;

import LLD.BNPLApp.entity.PurchaseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PurchaseRepository {
    private final Map<String, PurchaseEntity> purchases = new ConcurrentHashMap<>();

    public void save(PurchaseEntity purchase) {
        purchases.put(purchase.getPurchaseId(), purchase);
    }

    public void delete(String purchaseId) {
        purchases.remove(purchaseId);
    }

    public List<PurchaseEntity> findByUserId(String userId) {
        List<PurchaseEntity> result = new ArrayList<>();
        for (PurchaseEntity purchase : purchases.values()) {
            if (purchase.getUserId().equals(userId)) {
                result.add(purchase);
            }
        }
        return result;
    }
}
