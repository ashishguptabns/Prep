package LLD.RateLimiterApp.repository;

import java.util.List;
import java.util.Optional;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;

public interface RateLimitRuleStore {
    void save(RateLimitRuleEntity rule);

    Optional<RateLimitRuleEntity> findByClientAndResource(String clientId, String resourcePath);

    List<RateLimitRuleEntity> findAllByClientAndResource(String clientId, String resourcePath);
}
