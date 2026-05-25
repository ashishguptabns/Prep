package LLD.MessageBrokerApp.persistence;

import java.util.List;

import LLD.MessageBrokerApp.model.Message;

public interface MessageStore {
    Message persist(Message message);
    void remove(Message message);
    List<Message> loadAll();
    List<Message> fetch(String topic, int partition, long fromOffset);
}
