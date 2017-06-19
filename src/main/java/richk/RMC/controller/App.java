package richk.RMC.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.internal.parser.JSONParser;
import richk.RMC.model.Device;
import richk.RMC.model.ModelException;
import richk.RMC.swing.MainPanel;
import richk.RMC.util.Crypto;
import richk.RMC.view.View;

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
        LookAndFeel.initLookAndFeel("System","Metal");
        view = new MainPanel(this);
        network = new Network();
    }

    public List<Device> RefreshDevice(String url) throws ModelException{
        List<Device> deviceList = new ArrayList<Device>();
        String sDevicesList = null;
        Gson gson = new Gson();

        try {
            sDevicesList = network.GetURLContents(url);
        } catch (NetworkException e) {
            throw new ModelException(e);
        }

        Type listType = new TypeToken<ArrayList<Device>>(){}.getType();

        deviceList = gson.fromJson(sDevicesList, listType);

        return deviceList;
    }


}
