package it.richkmeli.RMC.controller;

public interface NetworkCallback {

    void onSuccess(String response);

    void onFailure(Exception e);
}
