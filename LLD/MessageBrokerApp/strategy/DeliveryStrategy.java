package LLD.MessageBrokerApp.strategy;

import java.util.Set;

import LLD.MessageBrokerApp.model.Message;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public interface DeliveryStrategy {
    void deliver(Message message, Set<Subscriber> subscribers);

    default void shutdown() {
        // No-op delivery shutdown by default
    }
}
