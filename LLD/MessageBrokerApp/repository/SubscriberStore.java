package LLD.MessageBrokerApp.repository;

import java.util.Set;

import LLD.MessageBrokerApp.subscriber.Subscriber;

public interface SubscriberStore {
    void register(String topic, Subscriber subscriber);
    void register(String topic, int partition, Subscriber subscriber);
    void unregister(String topic, Subscriber subscriber);
    void unregister(String topic, int partition, Subscriber subscriber);
    Set<Subscriber> getSubscribers(String topic, int partition);
}
