package LLD.RateLimiterApp.repository;

import java.util.List;

import LLD.RateLimiterApp.model.RequestLogEntity;

public interface RequestLogStore {
    void save(RequestLogEntity requestLog);

    List<RequestLogEntity> findByClientAndResource(String clientId, String resourcePath);
}
