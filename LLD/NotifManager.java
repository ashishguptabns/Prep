package LLD;

import java.util.LinkedList;
import java.util.Queue;

/*
Problem:

Design a notification system that can send notifications via multiple channels (Email, SMS, Push). Users can choose which types of notifications they want to receive.

Functional Requirements:

Users can subscribe/unsubscribe to specific notification types (e.g., promo, alerts).
The system can send messages via Email, SMS, and Push.
Supports scheduling and retry on failure.
*/

public class NotifManager {
    class Notification {
        Type type;
        String msg;
        String userId;
        String notifId;
        Channel channel;
    }

    Queue<Notification> smsQueue = new LinkedList<>();
    Queue<Notification> emailQueue = new LinkedList<>();

    enum Type {
        PROMO, ALERT;
    }

    enum Channel {
        EMAIL, SMS, PUSH
    }

    private void sendNotification(Notification notification) {

    }

    private void subscribe(String userId, Type type) {

    }
}
