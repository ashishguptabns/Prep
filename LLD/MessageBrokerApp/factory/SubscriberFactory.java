package LLD.MessageBrokerApp.factory;

import LLD.MessageBrokerApp.exception.MessageBrokerException;
import LLD.MessageBrokerApp.subscriber.PrintSubscriber;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public final class SubscriberFactory {
    private SubscriberFactory() {
    }

    public static Subscriber createPrintSubscriber(String subscriberId) {
        if (subscriberId == null || subscriberId.isBlank()) {
            throw new MessageBrokerException("Subscriber ID is required");
        }
        return new PrintSubscriber(subscriberId);
    }
}
