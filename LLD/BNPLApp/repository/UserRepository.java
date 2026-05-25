package LLD.BNPLApp.repository;

import LLD.BNPLApp.entity.UserEntity;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private final Map<String, UserEntity> users = new ConcurrentHashMap<>();

    public void save(UserEntity user) {
        users.put(user.getUserId(), user);
    }

    public Optional<UserEntity> findById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }
}
