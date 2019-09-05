package it.richkmeli.rmc.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.rmc.controller.network.Network;
import it.richkmeli.rmc.controller.network.SocketCallback;
import it.richkmeli.rmc.controller.network.SocketThread;
import it.richkmeli.rmc.model.Device;
import it.richkmeli.rmc.model.ModelException;
import it.richkmeli.rmc.swing.ListCallback;
import it.richkmeli.rmc.swing.RichkwareCallback;
import it.richkmeli.rmc.utils.Logger;
import it.richkmeli.rmc.utils.ResponseParser;
import it.richkmeli.rmc.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class Controller {

    private static final String SECURE_DATA_CLIENT = "secureDataClient";
    private static final String CLIENT_KEY = "clientKey";
    private static final String SERVER_RESPONSE = "serverResponse";

    private static final int SECURE_CONNECTION_MAX_ATTEMPT = 5;

    private Network network;
    private Crypto.Client cryptoClient;
    private static String clientID = null;
    private List<String> devicesList;
    private Map<Device, SocketThread> devicesMap;

    public Controller() {
        network = new Network();
        cryptoClient = new Crypto.Client();
    }

    public Network getNetwork() {
        return network;
    }

    public void login(String email, String password, RichkwareCallback callback) {

        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", Crypto.hashPassword(password, true));

        network.getRequest("LogIn", payload.toString(), cryptoClient, new NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                if (ResponseParser.isStatusOK(response))
                    callback.onSuccess(ResponseParser.parseMessage(response));
                else
                    callback.onFailure(ResponseParser.parseMessage(response));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public void logout(Boolean encryption, RichkwareCallback callback) {
        network.getRequest("LogOut", null, encryption ? cryptoClient : null, new NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                if (ResponseParser.isStatusOK(response))
                    callback.onSuccess(ResponseParser.parseMessage(response));
                else
                    callback.onFailure(ResponseParser.parseMessage(response));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public void userStatus(Boolean encryption, RichkwareCallback callback) {
        network.getRequest("user", null, encryption ? cryptoClient : null, new NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                if (ResponseParser.isStatusOK(response))
                    callback.onSuccess(ResponseParser.parseMessage(response));
                else
                    callback.onFailure(ResponseParser.parseMessage(response));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public void devicesList(boolean encryption, ListCallback callback) throws ModelException {
        String sDevicesList = null;
        Gson gson = new Gson();

        network.getRequest("devicesList", null, encryption ? cryptoClient : null, new NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                if (ResponseParser.isStatusOK(response)) {
                    Type listType = new TypeToken<ArrayList<Device>>() {
                    }.getType();
                    callback.onSuccess(gson.fromJson(ResponseParser.parseMessage(response), listType));
                } else
                    callback.onFailure(ResponseParser.parseMessage(response));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public void connectDevice(Device device) {
        devicesList = new ArrayList<>();
        devicesList.add(device.getName());
    }

//    //DONE
//    private void reverseCommand(ArrayList device, String commands, boolean encryption, RichkwareCallback callback) {
//        String[] commandsArray = commands.split("\n");
//        String encodedCommands = "";
//        for (int i = 0; i < commandsArray.length; i++) {
//            encodedCommands += (Base64.getEncoder().encodeToString(commandsArray[i].getBytes()));
//            if (i != commandsArray.length - 1)
//                encodedCommands += "##";
//        }
//        encodedCommands = Base64.getEncoder().encodeToString(encodedCommands.getBytes());
//        JSONObject jsonParameters = new JSONObject()
//                .put("device", device.getName())
//                .put("commands", encodedCommands);
//
//        network.putRequest("command", jsonParameters.toString(), encryption, new NetworkCallback() {
//            @Override
//            public void onSuccess(String response) {
//                if (ResponseParser.isStatusOK(response))
//                    callback.onSuccess(ResponseParser.parseMessage(response));
//                else
//                    callback.onFailure(ResponseParser.parseMessage(response));
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                callback.onFailure(e.getMessage());
//            }
//        });
//
//    }
//
//    //DONE
//    public void reverseCommand(ArrayList<String> commands, boolean encryption, ReverseCommandCallback callback) {
//        Map<String, String> responseMap = new HashMap<>();
//        for (int i = 0; i < devicesList.size(); i++) {
//            int finalI = i;
//            reverseCommand(devicesList.get(i), commands, encryption, new RichkwareCallback() {
//                @Override
//                public void onSuccess(String response) {
//                    reverseReturn(finalI, responseMap, callback);
//                }
//
//                @Override
//                public void onFailure(String response) {
//                    responseMap.put(devicesList.get(finalI), response);
//                    reverseReturn(finalI, responseMap, callback);
//                }
//            });
//        }
//    }

    public void connectDevice(List<Device> devices) {
        List<String> list = new ArrayList<>();
        for (Device device : devices) {
            String name = device.getName();
            list.add(name);
        }
        devicesList = list;
    }

//
//    private void reverseReturn(int i, Map<String, String> map, ReverseCommandCallback callback) {
//        if (i == devicesList.size()-1){
//            if(map.isEmpty()){
//               callback.onSuccess();
//            } else {
//                callback.onFailure(map);
//            }
//        }
//    }

    public void reverseCommand(String commands, boolean encryption, RichkwareCallback callback) {
        String[] commandsArray = commands.split("\n");
        StringBuilder encodedCommands = new StringBuilder();
        for (int i = 0; i < commandsArray.length; i++) {
            encodedCommands.append(Base64.getEncoder().encodeToString(commandsArray[i].getBytes()));
            if (i != commandsArray.length - 1)
                encodedCommands.append("##");
        }
        encodedCommands = new StringBuilder(Base64.getEncoder().encodeToString(encodedCommands.toString().getBytes()));
        JSONObject jsonParameters = new JSONObject()
                .put("devices", new JSONArray(devicesList))
                .put("commands", encodedCommands.toString());

        //TODO encryption
        network.putRequest("command", jsonParameters.toString(), null, new NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                if (ResponseParser.isStatusOK(response))
                    callback.onSuccess(ResponseParser.parseMessage(response));
                else
                    callback.onFailure(ResponseParser.parseMessage(response));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e.getMessage());
            }
        });

    }

    public void reverseCommandResponse(RichkwareCallback callback) {
        JSONObject jsonParameters = new JSONObject();

        jsonParameters.put("data0", devicesList.get(0));
        //TODO encryption
        network.getRequest("command", jsonParameters.toString(), cryptoClient, new NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                if (ResponseParser.isStatusOK(response))
                    callback.onSuccess(ResponseParser.parseMessage(response));
                else
                    callback.onFailure(ResponseParser.parseMessage(response));
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public void openSocket(Device device, boolean forceEncryption, RichkwareCallback callback) {
        devicesMap = new HashMap<>();
        network.openSocket(device.getIp(), device.getServerPort(), device.getEncryptionKey(), forceEncryption, new SocketCallback() {
            @Override
            public void onSuccess(SocketThread socketThread) {
                devicesMap.put(device, socketThread);
                callback.onSuccess("");
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    public void openSocket(List<Device> devices, boolean forceEncryption, RichkwareCallback callback) {
        for (Device device : devices) {
            openSocket(device, forceEncryption, callback);
        }
    }

    public void sendCommand(String command, RichkwareCallback callback) {
        for (SocketThread socketThread : devicesMap.values()) {
            socketThread.sendCommand(command, callback);
        }
    }

    public void closeSocket() {
        for (SocketThread socketThread : devicesMap.values()) {
            socketThread.disconnect();
        }
    }

    public void deleteCryptoState() {
        cryptoClient.reset();
        network.deleteSession();
    }

    private void asyncInit(Map<String, Object> map, int attempt, RichkwareCallback callback) { // callback chiamata allo stato 3
        String clientResponse = cryptoClient.init((File) map.get(SECURE_DATA_CLIENT), (String) map.get(CLIENT_KEY), (String) map.get(SERVER_RESPONSE));

        Logger.i(clientResponse);

        int clientState = new JSONObject(clientResponse).getInt("state");

        JSONObject parametersJson = new JSONObject();
        parametersJson.put("clientID", clientID);
        parametersJson.put("data", new JSONObject(clientResponse).getString("payload"));

        Logger.i("clientState: " + clientState);

        getNetwork().getRequestCompat("secureConnection", parametersJson.toString(), new NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                if (attempt < SECURE_CONNECTION_MAX_ATTEMPT) {

                    String clientResponse = cryptoClient.init((File) map.get(SECURE_DATA_CLIENT), (String) map.get(CLIENT_KEY), response);
                    int clientState = new JSONObject(clientResponse).getInt("state");
                    Logger.i("clientState: " + clientState);
                    if (clientState == -1) {
                        callback.onFailure("Corrupted state");
                    } else if (clientState != 3) {
                        map.remove(SERVER_RESPONSE);
                        map.put(SERVER_RESPONSE, response);
                        Logger.i(map.get(SECURE_DATA_CLIENT) + " " + (String) map.get(CLIENT_KEY) + " " + (String) map.get(SERVER_RESPONSE));
                        asyncInit(map, attempt + 1, callback);
                    } else {
                        //TODO chaiamata a se stesso se stato <3, oppure chiama callback
                        callback.onSuccess(String.valueOf(clientState));
                    }
                } else {
                    callback.onFailure("Connection timeout");
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    private void setClientID() {
        if (clientID == null) {
            String id = "RMC_" + Utils.getDeviceIdentifier();
            clientID = Base64.getUrlEncoder().encodeToString(id.getBytes());
        }
        Logger.i(Utils.getDeviceInfo());
    }

    public void initSecureConnection(RichkwareCallback callback) {
        Logger.i("initSecureConnection...");

        setClientID();

        //TODO verificare che non si leghi ad un unico server, altrimenti diverso file TXT, nome server nel file TXT
        // re-init to allow a connection to a different server
        cryptoClient = new Crypto.Client();

        File secureDataClient = new File("TESTsecureDataClient.txt");
        String clientKey = "testkeyClient";
        String serverResponse = "";

        Map<String, Object> map = new HashMap<>();
        map.put(SECURE_DATA_CLIENT, secureDataClient);
        map.put(CLIENT_KEY, clientKey);
        map.put(SERVER_RESPONSE, serverResponse);

        asyncInit(map, 0, callback);
    }
}
