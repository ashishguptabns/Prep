package LLD.MessageBrokerApp.strategy;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import LLD.MessageBrokerApp.exception.MessageBrokerException;
import LLD.MessageBrokerApp.model.Message;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public class BackPressureDeliveryStrategy implements DeliveryStrategy {
    private final ThreadPoolExecutor executor;

    public BackPressureDeliveryStrategy(int poolSize, int queueCapacity) {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity);
        this.executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, queue);
        this.executor.setRejectedExecutionHandler((r, exec) -> {
            try {
                exec.getQueue().put(r);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RejectedExecutionException("Interrupted while waiting for capacity", e);
            }
        });
    }

    @Override
    public void deliver(Message message, Set<Subscriber> subscribers) {
        for (Subscriber subscriber : subscribers) {
            executor.execute(() -> {
                try {
                    subscriber.onMessage(message);
                } catch (RuntimeException e) {
                    System.err.printf("Failed to deliver message to subscriber %s: %s%n", subscriber.getSubscriberId(), e.getMessage());
                }
            });
        }
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }
}
