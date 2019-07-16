package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.model.Device;

import java.util.List;

public interface ListCallback {

    void onSuccess(List<Device> response);

    void onFailure(String response);

}
