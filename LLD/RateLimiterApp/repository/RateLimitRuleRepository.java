package LLD.RateLimiterApp.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;

public class RateLimitRuleRepository implements RateLimitRuleStore {
    private final Map<String, RateLimitRuleEntity> rules = new ConcurrentHashMap<>();

    @Override
    public void save(RateLimitRuleEntity rule) {
        rules.put(buildKey(rule.getClientId(), rule.getResourcePath()), rule);
    }

    @Override
    public Optional<RateLimitRuleEntity> findByClientAndResource(String clientId,
            String resourcePath) {
        return Optional.ofNullable(rules.get(buildKey(clientId, resourcePath)));
    }

    private String buildKey(String clientId, String resourcePath) {
        return clientId + "::" + resourcePath;
    }
}
