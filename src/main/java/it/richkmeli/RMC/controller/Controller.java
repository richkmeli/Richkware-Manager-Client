package it.richkmeli.RMC.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.model.ModelException;
import it.richkmeli.RMC.utils.ResponseParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private Network network;

    public Controller(){
        network = new Network();
    }

    public Network getNetwork() {
        return network;
    }

    public List<Device> RefreshDevice(boolean encryption) throws ModelException {
        List<Device> deviceList = new ArrayList<Device>();
        String sDevicesList = null;
        Gson gson = new Gson();

        String response = deviceList(encryption);

        if(!ResponseParser.isStatusOK(response))
            return null;

        sDevicesList = ResponseParser.parseMessage(response);
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

    public String Login(String email, String password) {
        String result = null;
        try {
            result = network.GetURLContents("LogIn?email="+email+"&password="+password);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String User() {
        String result = null;
        try {
            result = network.GetURLContents("user");
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String deviceList(boolean encryption) throws NetworkException {
        String result = null;
        if (encryption) {
            result = network.GetEncryptedURLContents("devicesList");
        } else {
            result = network.GetURLContents("devicesList");
        }
        return result;
    }

}
