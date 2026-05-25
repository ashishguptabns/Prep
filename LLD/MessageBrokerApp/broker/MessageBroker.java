package LLD.MessageBrokerApp.broker;

import java.util.List;
import java.util.Set;

import LLD.MessageBrokerApp.model.Message;
import LLD.MessageBrokerApp.persistence.MessageStore;
import LLD.MessageBrokerApp.repository.SubscriberStore;
import LLD.MessageBrokerApp.strategy.DeliveryStrategy;
import LLD.MessageBrokerApp.strategy.SynchronousDeliveryStrategy;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public class MessageBroker {
    private final SubscriberStore registry;
    private final MessageStore messageStore;
    private final DeliveryStrategy deliveryStrategy;

    public MessageBroker(SubscriberStore registry, MessageStore messageStore) {
        this(registry, messageStore, new SynchronousDeliveryStrategy());
    }

    public MessageBroker(SubscriberStore registry, MessageStore messageStore, DeliveryStrategy deliveryStrategy) {
        this.registry = registry;
        this.messageStore = messageStore;
        this.deliveryStrategy = deliveryStrategy;
    }

    public void subscribe(String topic, Subscriber subscriber) {
        registry.register(topic, subscriber);
    }

    public void subscribe(String topic, int partition, Subscriber subscriber) {
        registry.register(topic, partition, subscriber);
    }

    public void unsubscribe(String topic, Subscriber subscriber) {
        registry.unregister(topic, subscriber);
    }

    public void unsubscribe(String topic, int partition, Subscriber subscriber) {
        registry.unregister(topic, partition, subscriber);
    }

    public void publish(Message message) {
        Message persistedMessage = messageStore.persist(message);
        Set<Subscriber> subscribers = registry.getSubscribers(persistedMessage.getTopic(), persistedMessage.getPartition());
        deliveryStrategy.deliver(persistedMessage, subscribers);
        messageStore.remove(persistedMessage);
    }

    public List<Message> fetchPending(String topic, int partition, long fromOffset) {
        return messageStore.fetch(topic, partition, fromOffset);
    }

    public void shutdown() {
        deliveryStrategy.shutdown();
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }
}
