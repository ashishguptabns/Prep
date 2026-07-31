package LLD.NotifApp.provider;

import LLD.NotifApp.model.Email;
import LLD.NotifApp.model.Msg;

public class EmailSender implements NotifProvider<Email> {

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
