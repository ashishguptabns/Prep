package LLD.MessageBrokerApp.model;

public class Message {
    private final String topic;
    private final int partition;
    private final long offset;
    private final String payload;

    public Message(String topic, int partition, long offset, String payload) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Topic is required");
        }
        if (partition < 0) {
            throw new IllegalArgumentException("Partition cannot be negative");
        }
        if (offset < -1) {
            throw new IllegalArgumentException("Offset cannot be less than -1");
        }
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.payload = payload;
    }

    public Message(String topic, String payload) {
        this(topic, 0, -1, payload);
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Message{topic='" + topic + "', partition=" + partition + ", offset=" + offset + ", payload='" + payload + "'}";
    }
}
