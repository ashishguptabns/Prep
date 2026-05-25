package LLD.MessageBrokerApp.strategy;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import LLD.MessageBrokerApp.model.Message;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public class AsynchronousDeliveryStrategy implements DeliveryStrategy {
    private final ExecutorService executor;

    public AsynchronousDeliveryStrategy() {
        this(Executors.newCachedThreadPool());
    }

    public AsynchronousDeliveryStrategy(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void deliver(Message message, Set<Subscriber> subscribers) {
        for (Subscriber subscriber : subscribers) {
            executor.submit(() -> {
                try {
                    subscriber.onMessage(message);
                } catch (RuntimeException e) {
                    System.err.printf("Async delivery failed for subscriber %s: %s%n", subscriber.getSubscriberId(), e.getMessage());
                }
            });
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
