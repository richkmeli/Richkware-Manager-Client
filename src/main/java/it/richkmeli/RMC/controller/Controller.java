package it.richkmeli.RMC.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.RMC.controller.network.*;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.model.ModelException;
import it.richkmeli.RMC.swing.PanelCallback;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    private Network network;

    public Controller() {
        network = new Network();
    }

    public Network getNetwork() {
        return network;
    }

    public List<Device> refreshDevice(boolean encryption) throws ModelException {
        List<Device> deviceList = new ArrayList<Device>();
        String sDevicesList = null;
        Gson gson = new Gson();

        String response = deviceList(encryption);

        if (!ResponseParser.isStatusOK(response))
            return null;

        sDevicesList = ResponseParser.parseMessage(response);
        Type listType = new TypeToken<ArrayList<Device>>() {
        }.getType();

        deviceList = gson.fromJson(sDevicesList, listType);

        return deviceList;
    }

//    public String SendCommand(String ip, String port, String encryptionKey, boolean forceEncryption, String command) throws ModelException {
//        String response = null;
//        try {
//            response = network.SendCommand(ip, port,encryptionKey, forceEncryption, command);
//        } catch (NetworkException e) {
//            throw new ModelException(e);
//        }
//        return response;
//    }

    public String login(String email, String password) {
        String result = null;
        try {
            result = network.GetURLContents("LogIn?email=" + email + "&password=" + password);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String logout() {
        String result = null;
        try {
            result = network.GetURLContents("LogOut");
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String userStatus() {
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


    private Map<Device, SocketThread> devicesMap;

    public void connect(Device device, boolean forceEncryption, PanelCallback callback) {
        devicesMap = new HashMap<>();
        openSocketDevice(device, forceEncryption, callback);
    }

    public void connect(List<Device> devices, boolean forceEncryption, PanelCallback callback) {
        devicesMap = new HashMap<>();
        for (Device device : devices) {
            openSocketDevice(device, forceEncryption, callback);
        }
    }

    private void openSocketDevice(Device device, boolean forceEncryption, PanelCallback callback) {
        network.openSocket(device.getIp(), device.getServerPort(), device.getEncryptionKey(), forceEncryption, new SocketCallback() {
            @Override
            public void onSuccess(SocketThread socketThread) {
                Logger.i(device.getIp() + ":" + device.getServerPort() + " - Connesso");
                devicesMap.put(device, socketThread);
                callback.onSuccess();
            }

            @Override
            public void onFailure(String error) {
                Logger.i(device.getIp() + ":" + device.getServerPort() + " - Non connesso: " + error);
                callback.onFailure(error);
            }
        });
    }

    public void sendCommand(String command, CommandCallback callback) {
        Logger.i("I'm in Controller, sendCommand.");
        for (SocketThread socketThread : devicesMap.values()) {
            socketThread.sendCommand(command, callback);
        }
    }

    public void reverseCommand(Device device, String command) {
        try {
            network.reverseCommand(device, command);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
    }

    public void reverseCommand(List<Device> devices, String command) {
        for (Device device : devices)
            reverseCommand(device, command);
    }

    public String reverseCommandResponse(Device device) {
        String result = null;
        try {
            Logger.i("Request reverseCommandResponse " + " " + network.toString());
            result = network.GetURLContents("command?data0=" + device.getName() + "&data1=client");
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String login(){
        String jsonResponse = null;
        return jsonResponse;
    }

}

//    public String checkResponse(Device device){
//        String result = "";
//        try{
//            Logger.i("I'm checking for response...");
////            result = network.GetURLContents("comm")
//        } catch (NetworkException e) {
//            e.printStackTrace();
//        }
//
//    }
