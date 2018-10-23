package it.richkmeli.RMC.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.model.ModelException;
import it.richkmeli.RMC.view.View;
import it.richkmeli.RMC.swing.MainPanel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by richk on 25/05/17.
 */
public class App implements Runnable {
    protected View view;    // comunicazione da controller a view
    private Network network;

    public void run() {
        LookAndFeel.initLookAndFeel("System", "Metal");
        view = new MainPanel(this);
        network = new Network();
    }

    public List<Device> RefreshDevice(String url, boolean encryption) throws ModelException {
        List<Device> deviceList = new ArrayList<Device>();
        String sDevicesList = null;
        Gson gson = new Gson();

        try {
            if (encryption) {
                sDevicesList = network.GetEncryptedURLContents(url);
            } else {
                sDevicesList = network.GetURLContents(url);
            }
        } catch (NetworkException e) {
            throw new ModelException(e);
        }

        Type listType = new TypeToken<ArrayList<Device>>() {
        }.getType();

        deviceList = gson.fromJson(sDevicesList, listType);

        return deviceList;
    }

    public String SendCommand(String ip, String port, String encryptionKey, boolean forceEncryption, String command) throws ModelException {
        String response = null;
        try {
            response = network.SendCommand(ip, port,encryptionKey, forceEncryption, command);
        } catch (NetworkException e) {
            throw new ModelException(e);
        }
        return response;
    }

}
