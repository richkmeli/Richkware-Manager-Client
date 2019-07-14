package it.richkmeli.RMC.controller.network;

public interface SocketCallback {

    public void onSuccess(SocketThread socketThread);

    public void onFailure(Exception e);

}
