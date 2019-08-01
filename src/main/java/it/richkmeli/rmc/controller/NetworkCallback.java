package it.richkmeli.rmc.controller;

public interface NetworkCallback {

    void onSuccess(String response);

    void onFailure(Exception e);
}
