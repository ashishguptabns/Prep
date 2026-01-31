package LLD;

import LLD.NotifApp.Email;
import LLD.NotifApp.EmailSender;
import LLD.NotifApp.Msg;
import LLD.NotifApp.NotifProvider;
import LLD.NotifApp.NotifService;
import LLD.NotifApp.Phone;
import LLD.NotifApp.PhoneSender;
import LLD.NotifApp.SenderStrategy;
import LLD.NotifApp.Strategy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NotifApp {
    interface Msg {
        String msg();

        String receiver();
    }

    record Email(String msg, String receiver) implements Msg {
    };

    record Phone(String msg, String receiver) implements Msg {
    };

    class SenderStrategy implements Strategy {
        @Override
        public void send(Msg msg) {

        }
    }

    interface Strategy {
        void send(Msg msg);
    }

    Strategy senderStrategy = new SenderStrategy();

    void send(Msg msg) {
        senderStrategy.send(msg);
    }

    interface NotifProvider<T extends Msg> {
        void send(Msg msg);

        Class<T> getType();
    }

    class EmailSender implements NotifProvider<Email> {
        @Override
        public void send(Msg msg) {
            if (msg instanceof Email) {
                System.out.println(msg);
            }
        }

        @Override
        public Class<Email> getType() {
            return Email.class;
        }
    }

    class PhoneSender implements NotifProvider<Phone> {
        @Override
        public void send(Msg msg) {
            if (msg instanceof Phone) {
                System.out.println(msg);
            }
        }

        @Override
        public Class<Phone> getType() {
            return Phone.class;
        }
    }

    class NotifService {
        final Map<Class<?>, NotifProvider> providers = new HashMap<>();

        void addProvider(NotifProvider provider) {
            providers.put(provider.getType(), provider);
        }

        void send(Msg msg) {
            NotifProvider provider = providers.get(msg.getClass());

            Optional.ofNullable(provider)
                    .ifPresentOrElse(
                            p -> p.send(msg),
                            () -> {
                                throw new UnsupportedOperationException(
                                        "No provider registered for " + msg.getClass().getSimpleName());
                            });
        }
    }

    void run() {
        NotifService service = new NotifService();
        service.addProvider(new EmailSender());
        service.addProvider(new PhoneSender());
        service.send(new Email("xyz", "xyz"));
        service.send(new Phone("xyz", "xyz"));
    }

    public static void main(String[] ar) {
        NotifApp app = new NotifApp();
        app.run();
    }
}
