package it.richkmeli.RMC.swing;

import it.richkmeli.RMC.model.Device;

import java.util.List;

public interface ListCallback {

    public void onSuccess(List<Device> response);

    public void onFailure(String response);

}
