package LLD.NotifApp;

import LLD.NotifApp.model.Email;
import LLD.NotifApp.model.Phone;
import LLD.NotifApp.provider.EmailSender;
import LLD.NotifApp.provider.PhoneSender;
import LLD.NotifApp.repo.ProviderRepository;
import LLD.NotifApp.service.NotifService;

public class Driver {

    public static void main(String[] args) {
        Driver app = new Driver();
        app.run();
    }

    void run() {
        runNormal();
    }

    private void runNormal() {
        System.out.println("Normal");

        ProviderRepository providerRepository = new ProviderRepository();
        NotifService service = new NotifService(providerRepository);

        service.addProvider(new EmailSender());
        service.addProvider(new PhoneSender());

        service.send(new Email("xyz", "xyz"));
        service.send(new Phone("xyz", "xyz"));
    }
}
