package LLD.SlotBookingApp.repository;

import LLD.SlotBookingApp.entity.CenterEntity;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CenterRepository {
    private final Map<String, CenterEntity> centers = new ConcurrentHashMap<>();

    public void save(CenterEntity center) {
        centers.put(center.getCenterId(), center);
    }

    public Optional<CenterEntity> findById(String centerId) {
        return Optional.ofNullable(centers.get(centerId));
    }
}
