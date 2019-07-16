package it.richkmeli.RMC.controller.network;

public interface SocketCallback {

    void onSuccess(SocketThread socketThread);

    void onFailure(Exception e);

}
