package LLD.RateLimiterApp.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import LLD.RateLimiterApp.model.RequestLogEntity;

public class RequestLogRepository implements RequestLogStore {
    private final Map<String, CopyOnWriteArrayList<RequestLogEntity>> logs = new ConcurrentHashMap<>();

    @Override
    public void save(RequestLogEntity requestLog) {
        logs.computeIfAbsent(buildKey(requestLog.getClientId(), requestLog.getResourcePath()),
                key -> new CopyOnWriteArrayList<>()).add(requestLog);
    }

    @Override
    public List<RequestLogEntity> findByClientAndResource(String clientId, String resourcePath) {
        return new ArrayList<>(logs.getOrDefault(buildKey(clientId, resourcePath),
                new CopyOnWriteArrayList<>()));
    }

    private String buildKey(String clientId, String resourcePath) {
        return clientId + "::" + resourcePath;
    }
}
