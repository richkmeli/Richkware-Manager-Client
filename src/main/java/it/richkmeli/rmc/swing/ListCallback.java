package it.richkmeli.rmc.swing;

import it.richkmeli.rmc.model.Device;

import java.util.List;

public interface ListCallback {

    void onSuccess(List<Device> response);

    void onFailure(String response);

}
