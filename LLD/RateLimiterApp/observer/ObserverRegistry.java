package LLD.RateLimiterApp.observer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import LLD.RateLimiterApp.model.RateLimitResult;
import LLD.RateLimiterApp.model.Request;

public class ObserverRegistry {

    private final List<RateLimitObserver> observers;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    public ObserverRegistry(List<RateLimitObserver> observers) {
        this.observers = observers;
    }

    public void notifyObservers(String limiterName, Request request, RateLimitResult result) {
        for (RateLimitObserver observer : observers) {
            executor.submit(() -> observer.onDecision(limiterName, request, result));
        }
    }
}
