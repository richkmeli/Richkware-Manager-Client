package it.richkmeli.RMC.controller;

public interface NetworkCallback {

    public void onSuccess(String response);

    public void onFailure(Exception e);
}
