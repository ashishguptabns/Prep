package LLD.MessageBrokerApp.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import LLD.MessageBrokerApp.exception.MessageBrokerException;
import LLD.MessageBrokerApp.subscriber.PrintSubscriber;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public class FileBackedSubscriberRegistry implements SubscriberStore {
    private static final String DELIMITER = "\t";

    private final Map<String, Set<Subscriber>> topicSubscriptions = new ConcurrentHashMap<>();
    private final Map<TopicPartitionKey, Set<Subscriber>> partitionSubscriptions = new ConcurrentHashMap<>();
    private final Path filePath;

    public FileBackedSubscriberRegistry(String fileName) {
        try {
            this.filePath = Path.of(fileName);
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            loadFromFile();
        } catch (IOException e) {
            throw new MessageBrokerException("Failed to initialize subscriber persistence", e);
        }
    }

    @Override
    public synchronized void register(String topic, Subscriber subscriber) {
        register(topic, -1, subscriber);
    }

    @Override
    public synchronized void register(String topic, int partition, Subscriber subscriber) {
        if (topic == null || topic.isBlank()) {
            throw new MessageBrokerException("Topic is required");
        }
        if (subscriber == null) {
            throw new MessageBrokerException("Subscriber is required");
        }
        if (partition < -1) {
            throw new MessageBrokerException("Partition cannot be less than -1");
        }
        if (partition < 0) {
            topicSubscriptions.compute(topic, (key, set) -> {
                if (set == null) {
                    set = Collections.synchronizedSet(new HashSet<>());
                }
                set.add(subscriber);
                return set;
            });
        } else {
            TopicPartitionKey key = new TopicPartitionKey(topic, partition);
            partitionSubscriptions.compute(key, (k, set) -> {
                if (set == null) {
                    set = Collections.synchronizedSet(new HashSet<>());
                }
                set.add(subscriber);
                return set;
            });
        }
        persistAll();
    }

    @Override
    public synchronized void unregister(String topic, Subscriber subscriber) {
        unregister(topic, -1, subscriber);
    }

    @Override
    public synchronized void unregister(String topic, int partition, Subscriber subscriber) {
        if (topic == null || topic.isBlank()) {
            throw new MessageBrokerException("Topic is required");
        }
        if (subscriber == null) {
            throw new MessageBrokerException("Subscriber is required");
        }
        if (partition < 0) {
            Set<Subscriber> subscribers = topicSubscriptions.get(topic);
            if (subscribers != null) {
                subscribers.remove(subscriber);
            }
        } else {
            TopicPartitionKey key = new TopicPartitionKey(topic, partition);
            Set<Subscriber> subscribers = partitionSubscriptions.get(key);
            if (subscribers != null) {
                subscribers.remove(subscriber);
            }
        }
        persistAll();
    }

    @Override
    public Set<Subscriber> getSubscribers(String topic, int partition) {
        if (topic == null || topic.isBlank()) {
            throw new MessageBrokerException("Topic is required");
        }
        Set<Subscriber> result = new HashSet<>();
        result.addAll(topicSubscriptions.getOrDefault(topic, Collections.emptySet()));
        if (partition >= 0) {
            TopicPartitionKey key = new TopicPartitionKey(topic, partition);
            result.addAll(partitionSubscriptions.getOrDefault(key, Collections.emptySet()));
        }
        return result;
    }

    private void loadFromFile() {
        try {
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(DELIMITER, 4);
                if (parts.length != 4) {
                    continue;
                }
                String topic = decode(parts[0]);
                int partition = Integer.parseInt(parts[1]);
                String subscriberId = decode(parts[3]);
                register(topic, partition, new PrintSubscriber(subscriberId));
            }
        } catch (IOException e) {
            throw new MessageBrokerException("Failed to load subscriber persistence", e);
        }
    }

    private void persistAll() {
        try {
            Set<String> lines = new HashSet<>();
            for (String topic : topicSubscriptions.keySet()) {
                for (Subscriber subscriber : topicSubscriptions.get(topic)) {
                    lines.add(encode(topic) + DELIMITER + (-1) + DELIMITER + 0 + DELIMITER + encode(subscriber.getSubscriberId()));
                }
            }
            for (Map.Entry<TopicPartitionKey, Set<Subscriber>> entry : partitionSubscriptions.entrySet()) {
                TopicPartitionKey key = entry.getKey();
                for (Subscriber subscriber : entry.getValue()) {
                    lines.add(encode(key.topic) + DELIMITER + key.partition + DELIMITER + 1 + DELIMITER + encode(subscriber.getSubscriberId()));
                }
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MessageBrokerException("Failed to persist subscriber registrations", e);
        }
    }

    private String encode(String value) {
        return value.replace("\\", "\\\\").replace(DELIMITER, "\\t");
    }

    private String decode(String value) {
        return value.replace("\\t", DELIMITER).replace("\\\\", "\\");
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
