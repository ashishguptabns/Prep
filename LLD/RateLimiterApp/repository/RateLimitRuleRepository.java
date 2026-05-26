package LLD.RateLimiterApp.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

import LLD.RateLimiterApp.entity.RateLimitRuleEntity;
import LLD.RateLimiterApp.model.RateLimitScope;

public class RateLimitRuleRepository implements RateLimitRuleStore {
    private final Map<String, CopyOnWriteArrayList<RateLimitRuleEntity>> rules = new ConcurrentHashMap<>();

    @Override
    public void save(RateLimitRuleEntity rule) {
        rules.computeIfAbsent(buildKey(rule.getClientId(), rule.getResourcePath()),
                key -> new CopyOnWriteArrayList<>()).add(rule);
    }

    @Override
    public Optional<RateLimitRuleEntity> findByClientAndResource(String clientId,
            String resourcePath) {
        return findAllByClientAndResource(clientId, resourcePath).stream().findFirst();
    }

    @Override
    public List<RateLimitRuleEntity> findAllByClientAndResource(String clientId,
            String resourcePath) {
        Map<String, RateLimitRuleEntity> matchingRules = new LinkedHashMap<>();

        for (RateLimitRuleEntity rule : rules.getOrDefault(buildKey(clientId, resourcePath),
                new CopyOnWriteArrayList<>())) {
            matchingRules.put(rule.getRuleId(), rule);
        }

        for (CopyOnWriteArrayList<RateLimitRuleEntity> bucket : rules.values()) {
            for (RateLimitRuleEntity rule : bucket) {
                if (rule.getScope() == RateLimitScope.GLOBAL
                        && rule.getResourcePath().equals(resourcePath)) {
                    matchingRules.put(rule.getRuleId(), rule);
                }
            }
        }

        return new ArrayList<>(matchingRules.values());
    }

    private String buildKey(String clientId, String resourcePath) {
        return clientId + "::" + resourcePath;
    }
}
