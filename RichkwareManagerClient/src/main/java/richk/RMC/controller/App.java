package richk.RMC.controller;

import jdk.nashorn.internal.parser.JSONParser;
import richk.RMC.model.Device;
import richk.RMC.swing.MainPanel;
import richk.RMC.view.View;

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

    public String GetDevicesList(String url) {
        String sDevicesList = network.GetURLContents(url);
        System.out.println(sDevicesList);
        return sDevicesList;
    }


}
