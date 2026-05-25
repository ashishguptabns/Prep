package LLD.MessageBrokerApp.model;

import java.util.Objects;

public class TopicPartition {
    private final String topic;
    private final int partition;

    public TopicPartition(String topic, int partition) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Topic is required");
        }
        if (partition < 0) {
            throw new IllegalArgumentException("Partition cannot be negative");
        }
        this.topic = topic;
        this.partition = partition;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TopicPartition)) return false;
        TopicPartition that = (TopicPartition) o;
        return partition == that.partition && topic.equals(that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition);
    }

    @Override
    public String toString() {
        return "TopicPartition{topic='" + topic + "', partition=" + partition + "}";
    }
}
