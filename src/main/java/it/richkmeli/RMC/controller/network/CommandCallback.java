package it.richkmeli.RMC.controller.network;

public interface CommandCallback {

    public void onSuccess(String response);

    public void onFailure(String error);

}
