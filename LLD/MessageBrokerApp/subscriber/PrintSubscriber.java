package LLD.MessageBrokerApp.subscriber;

import LLD.MessageBrokerApp.model.Message;

public class PrintSubscriber implements Subscriber {
    private final String subscriberId;

    public PrintSubscriber(String subscriberId) {
        if (subscriberId == null || subscriberId.isBlank()) {
            throw new IllegalArgumentException("Subscriber ID is required");
        }
        this.subscriberId = subscriberId;
    }

    @Override
    public void onMessage(Message message) {
        System.out.printf("[%s] received %s%n", subscriberId, message);
    }

    @Override
    public String getSubscriberId() {
        return subscriberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrintSubscriber)) {
            return false;
        }
        PrintSubscriber that = (PrintSubscriber) o;
        return subscriberId.equals(that.subscriberId);
    }

    @Override
    public int hashCode() {
        return subscriberId.hashCode();
    }
}
