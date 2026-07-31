package LLD.NotifApp.service;

import LLD.NotifApp.model.Msg;
import LLD.NotifApp.provider.NotifProvider;
import LLD.NotifApp.repo.ProviderRepository;
import java.util.Optional;

public class NotifService {

    private final ProviderRepository providerRepository;

    public NotifService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public void addProvider(NotifProvider<?> provider) {
        providerRepository.add(provider);
    }

    public void send(Msg msg) {
        NotifProvider<?> provider = providerRepository.get(msg.getClass());

        Optional.ofNullable(provider)
                .ifPresentOrElse(
                        p -> p.send(msg),
                        () -> {
                            throw new UnsupportedOperationException(
                                    "No provider registered for " + msg.getClass().getSimpleName());
                        });
    }
}
