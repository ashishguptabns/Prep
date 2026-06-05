package LLD.MessageBrokerApp.service;

import java.util.List;

import LLD.MessageBrokerApp.broker.MessageBroker;
import LLD.MessageBrokerApp.exception.MessageBrokerException;
import LLD.MessageBrokerApp.factory.MessageFactory;
import LLD.MessageBrokerApp.model.Message;
import LLD.MessageBrokerApp.persistence.FileMessageStore;
import LLD.MessageBrokerApp.persistence.MessageStore;
import LLD.MessageBrokerApp.repository.SubscriberRegistry;
import LLD.MessageBrokerApp.strategy.SynchronousDeliveryStrategy;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public class MessageBrokerService {

    private final MessageBroker messageBroker;
    private final MessageStore messageStore;

    public MessageBrokerService() {
        this.messageStore = new FileMessageStore("messages.log");
        this.messageBroker = new MessageBroker(new SubscriberRegistry(),
                messageStore, new SynchronousDeliveryStrategy());
    }

    public void registerSubscriber(String topic, Subscriber subscriber) {
        messageBroker.subscribe(topic, subscriber);
    }

    public void registerSubscriber(String topic, int partition, Subscriber subscriber) {
        messageBroker.subscribe(topic, partition, subscriber);
    }

    public void removeSubscriber(String topic, Subscriber subscriber) {
        messageBroker.unsubscribe(topic, subscriber);
    }

    public void removeSubscriber(String topic, int partition, Subscriber subscriber) {
        messageBroker.unsubscribe(topic, partition, subscriber);
    }

    public void publish(String topic, String payload) {
        messageBroker.publish(MessageFactory.create(topic, payload));
    }

    public void publish(String topic, int partition, String payload) {
        messageBroker.publish(MessageFactory.create(topic, partition, payload));
    }

    public void publish(String topic, String partitionKey, String payload, int partitionCount) {
        messageBroker.publish(MessageFactory.create(topic, partitionKey, payload, partitionCount));
    }

    public void publish(Message message) {
        if (message == null) {
            throw new MessageBrokerException("Message is required");
        }
        messageBroker.publish(message);
    }

    public List<Message> loadPersistedMessages() {
        return messageStore.loadAll();
    }

    public List<Message> fetchPendingMessages(String topic, int partition, long fromOffset) {
        return messageBroker.fetchPending(topic, partition, fromOffset);
    }

    public void shutdown() {
        messageBroker.shutdown();
    }
}
