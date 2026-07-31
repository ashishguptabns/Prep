package LLD.NotifApp.repo;

import LLD.NotifApp.provider.NotifProvider;
import java.util.HashMap;
import java.util.Map;

public class ProviderRepository {

    private final Map<Class<?>, NotifProvider<?>> providers = new HashMap<>();

    public void add(NotifProvider<?> provider) {
        providers.put(provider.getType(), provider);
    }

    public NotifProvider<?> get(Class<?> type) {
        return providers.get(type);
    }
}
