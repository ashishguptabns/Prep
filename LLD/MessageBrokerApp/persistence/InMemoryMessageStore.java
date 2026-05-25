package LLD.MessageBrokerApp.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import LLD.MessageBrokerApp.model.Message;

public class InMemoryMessageStore implements MessageStore {
    private final List<Message> messages = Collections.synchronizedList(new ArrayList<>());

    @Override
    public synchronized Message persist(Message message) {
        long offset = messages.stream()
                .filter(existing -> existing.getTopic().equals(message.getTopic())
                        && existing.getPartition() == message.getPartition())
                .mapToLong(Message::getOffset)
                .max()
                .orElse(-1L) + 1;
        Message persisted = new Message(message.getTopic(), message.getPartition(), offset, message.getPayload());
        messages.add(persisted);
        return persisted;
    }

    @Override
    public synchronized void remove(Message message) {
        messages.remove(message);
    }

    @Override
    public synchronized List<Message> loadAll() {
        return new ArrayList<>(messages);
    }

    @Override
    public synchronized List<Message> fetch(String topic, int partition, long fromOffset) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message.getTopic().equals(topic)
                    && message.getPartition() == partition
                    && message.getOffset() >= fromOffset) {
                result.add(message);
            }
        }
        return result;
    }
}
