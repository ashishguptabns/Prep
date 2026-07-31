package LLD.NotifApp.provider;

import LLD.NotifApp.model.Msg;
import LLD.NotifApp.model.Phone;

public class PhoneSender implements NotifProvider<Phone> {

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
