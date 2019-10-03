package it.richkmeli.rmc.controller.network;

public interface SocketCallback {

    void onSuccess(SocketThread socketThread);

    void onFailure(Exception e);

}
