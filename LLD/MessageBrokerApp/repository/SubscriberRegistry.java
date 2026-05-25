package LLD.MessageBrokerApp.repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import LLD.MessageBrokerApp.exception.MessageBrokerException;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public class SubscriberRegistry implements SubscriberStore {
    private final Map<String, Set<Subscriber>> topicSubscribers = new ConcurrentHashMap<>();
    private final Map<TopicPartitionKey, Set<Subscriber>> partitionSubscribers = new ConcurrentHashMap<>();

    @Override
    public void register(String topic, Subscriber subscriber) {
        register(topic, -1, subscriber);
    }

    @Override
    public void register(String topic, int partition, Subscriber subscriber) {
        if (topic == null || topic.isBlank()) {
            throw new MessageBrokerException("Topic is required");
        }
        if (subscriber == null) {
            throw new MessageBrokerException("Subscriber is required");
        }
        if (partition < -1) {
            throw new MessageBrokerException("Partition cannot be less than -1");
        }

        Map<String, Set<Subscriber>> target = (partition < 0) ? topicSubscribers : null;
        if (partition < 0) {
            topicSubscribers.compute(topic, (key, set) -> {
                if (set == null) {
                    set = Collections.synchronizedSet(new HashSet<>());
                }
                set.add(subscriber);
                return set;
            });
        } else {
            TopicPartitionKey key = new TopicPartitionKey(topic, partition);
            partitionSubscribers.compute(key, (k, set) -> {
                if (set == null) {
                    set = Collections.synchronizedSet(new HashSet<>());
                }
                set.add(subscriber);
                return set;
            });
        }
    }

    @Override
    public void unregister(String topic, Subscriber subscriber) {
        unregister(topic, -1, subscriber);
    }

    @Override
    public void unregister(String topic, int partition, Subscriber subscriber) {
        if (topic == null || topic.isBlank()) {
            throw new MessageBrokerException("Topic is required");
        }
        if (subscriber == null) {
            throw new MessageBrokerException("Subscriber is required");
        }

        if (partition < 0) {
            Set<Subscriber> subscribers = topicSubscribers.get(topic);
            if (subscribers != null) {
                subscribers.remove(subscriber);
            }
        } else {
            TopicPartitionKey key = new TopicPartitionKey(topic, partition);
            Set<Subscriber> subscribers = partitionSubscribers.get(key);
            if (subscribers != null) {
                subscribers.remove(subscriber);
            }
        }
    }

    @Override
    public Set<Subscriber> getSubscribers(String topic, int partition) {
        if (topic == null || topic.isBlank()) {
            throw new MessageBrokerException("Topic is required");
        }
        Set<Subscriber> result = new HashSet<>();
        result.addAll(topicSubscribers.getOrDefault(topic, Collections.emptySet()));
        if (partition >= 0) {
            TopicPartitionKey key = new TopicPartitionKey(topic, partition);
            result.addAll(partitionSubscribers.getOrDefault(key, Collections.emptySet()));
        }
        return result;
    }

    private static class TopicPartitionKey {
        private final String topic;
        private final int partition;

        TopicPartitionKey(String topic, int partition) {
            this.topic = topic;
            this.partition = partition;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TopicPartitionKey)) return false;
            TopicPartitionKey that = (TopicPartitionKey) o;
            return partition == that.partition && topic.equals(that.topic);
        }

        @Override
        public int hashCode() {
            return topic.hashCode() * 31 + partition;
        }
    }
}
