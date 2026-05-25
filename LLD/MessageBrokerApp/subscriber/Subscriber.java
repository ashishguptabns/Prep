package LLD.MessageBrokerApp.subscriber;

import LLD.MessageBrokerApp.model.Message;

public interface Subscriber {
    void onMessage(Message message);
    String getSubscriberId();
}
