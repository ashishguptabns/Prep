package LLD.NotifApp.provider;

import LLD.NotifApp.model.Msg;

public interface NotifProvider<T extends Msg> {
    void send(Msg msg);

    Class<T> getType();
}
