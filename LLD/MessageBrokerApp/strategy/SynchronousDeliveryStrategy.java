package LLD.MessageBrokerApp.strategy;

import java.util.Set;

import LLD.MessageBrokerApp.model.Message;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public class SynchronousDeliveryStrategy implements DeliveryStrategy {
    @Override
    public void deliver(Message message, Set<Subscriber> subscribers) {
        for (Subscriber subscriber : subscribers) {
            subscriber.onMessage(message);
        }
    }
}
