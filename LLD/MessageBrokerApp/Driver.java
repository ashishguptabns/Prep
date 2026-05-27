package LLD.MessageBrokerApp;

import LLD.MessageBrokerApp.factory.SubscriberFactory;
import LLD.MessageBrokerApp.model.Message;
import LLD.MessageBrokerApp.persistence.FileMessageStore;
import LLD.MessageBrokerApp.repository.SubscriberRegistry;
import LLD.MessageBrokerApp.service.MessageBrokerService;
import LLD.MessageBrokerApp.strategy.SynchronousDeliveryStrategy;
import LLD.MessageBrokerApp.subscriber.Subscriber;

public class Driver {

    public static void main(String[] args) {
        MessageBrokerService brokerService = new MessageBrokerService(new SubscriberRegistry(),
                new FileMessageStore("messages.log"), new SynchronousDeliveryStrategy());

        Subscriber emailSubscriber = SubscriberFactory.createPrintSubscriber("EmailSubscriber");
        Subscriber dashboardSubscriber = SubscriberFactory.createPrintSubscriber("DashboardSubscriber");

        brokerService.registerSubscriber("order.created", 0, emailSubscriber);
        brokerService.registerSubscriber("order.created", 1, dashboardSubscriber);
        brokerService.registerSubscriber("payment.completed", dashboardSubscriber);

        brokerService.publish("order.created", "Asha", "Order #12345 created for user Asha", 2);
        brokerService.publish("order.created", 0, "Order #12345 created for user Asha on partition 0");
        brokerService.publish("order.created", 1, "Order #12346 created for user Dev on partition 1");
        brokerService.publish("payment.completed", "payment-1", "Payment for order #12345 completed", 1);

        System.out.println("Persisted messages:");
        for (Message message : brokerService.loadPersistedMessages()) {
            System.out.println(message);
        }

        System.out.println("Pending partition 0 from offset 0:");
        for (Message message : brokerService.fetchPendingMessages("order.created", 0, 0)) {
            System.out.println(message);
        }

        brokerService.shutdown();
    }
}
