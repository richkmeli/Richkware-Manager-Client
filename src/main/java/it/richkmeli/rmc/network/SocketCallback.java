package it.richkmeli.rmc.network;

public interface SocketCallback {

    void onSuccess(SocketThread socketThread);

    void onFailure(Exception e);

}
