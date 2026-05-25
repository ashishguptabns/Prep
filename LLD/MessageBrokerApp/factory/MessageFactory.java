package LLD.MessageBrokerApp.factory;

import LLD.MessageBrokerApp.exception.MessageBrokerException;
import LLD.MessageBrokerApp.model.Message;

public final class MessageFactory {
    private MessageFactory() {
        // Factory class should not be instantiated
    }

    public static Message create(String topic, String payload) {
        validate(topic, payload);
        return new Message(topic, payload);
    }

    public static Message create(String topic, int partition, String payload) {
        validate(topic, payload);
        if (partition < 0) {
            throw new MessageBrokerException("Partition cannot be negative");
        }
        return new Message(topic, partition, -1, payload);
    }

    public static Message create(String topic, String partitionKey, String payload, int partitionCount) {
        validate(topic, payload);
        if (partitionKey == null) {
            partitionKey = "";
        }
        if (partitionCount <= 0) {
            throw new MessageBrokerException("Partition count must be positive");
        }
        int partition = Math.abs(partitionKey.hashCode()) % partitionCount;
        return new Message(topic, partition, -1, payload);
    }

    private static void validate(String topic, String payload) {
        if (topic == null || topic.isBlank()) {
            throw new MessageBrokerException("Message topic is required");
        }
        if (payload == null) {
            throw new MessageBrokerException("Message payload is required");
        }
    }
}
