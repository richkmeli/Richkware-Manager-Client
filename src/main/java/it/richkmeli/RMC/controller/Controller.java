package it.richkmeli.RMC.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.RMC.controller.network.Network;
import it.richkmeli.RMC.controller.network.NetworkException;
import it.richkmeli.RMC.controller.network.SocketCallback;
import it.richkmeli.RMC.controller.network.SocketThread;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.model.ModelException;
import it.richkmeli.RMC.swing.ListCallback;
import it.richkmeli.RMC.swing.RichkwareCallback;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;
import it.richkmeli.jcrypto.Crypto;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {

    private Network network;
    private Crypto.Client cryptoClient;

    public Controller() {
        network = new Network();
        cryptoClient = new Crypto.Client();
    }

    public Network getNetwork() {
        return network;
    }

    /**
     * REVERSE COMMAND (SERVER)
     */

    private List<String> devicesList;
    /**
     * DIRECT COMMANDS (SOCKET)
     */

    private Map<Device, SocketThread> devicesMap;

    //DONE
    public void login(String email, String password, RichkwareCallback callback) {
        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", password);

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

    //DONE
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

    //DONE
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

    //DONE
    public void connectDevice(List<Device> devices) {
        devicesList = devices.stream().map(Device::getName).collect(Collectors.toList());
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

    //DONE
    public void reverseCommand(String commands, boolean encryption, RichkwareCallback callback) {
        String[] commandsArray = commands.split("\n");
        String encodedCommands = "";
        for (int i = 0; i < commandsArray.length; i++) {
            encodedCommands += (Base64.getEncoder().encodeToString(commandsArray[i].getBytes()));
            if (i != commandsArray.length - 1)
                encodedCommands += "##";
        }
        encodedCommands = Base64.getEncoder().encodeToString(encodedCommands.getBytes());
        JSONObject jsonParameters = new JSONObject()
                .put("devices", new JSONArray(devicesList))
                .put("commands", encodedCommands);

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

    public void reverseCommandResponse(Device device, RichkwareCallback callback) {
        JSONObject jsonParameters = new JSONObject();
        jsonParameters.put("data0", device.getName());
        jsonParameters.put("data1", "client");
        //TODO encryption
        network.getRequest("command", jsonParameters.toString(), null, new NetworkCallback() {
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
    }

    //todo rimuovere dipendenza da swing, ma fare barra con stato del jcrypto
    public void initSecureConnection(String clientID, JLabel encStateValue) {
        Logger.i("initSecureConnection...");
        //TODO verificare che non si leghi ad un unico server, altrimenti diverso file TXT, nome server nel file TXT
        // re-init to allow a connection to a different server
        cryptoClient = new Crypto.Client();

        File secureDataClient = new File("TESTsecureDataClient.txt");
        String clientKey = "testkeyClient";
        int maxAttempts = 5;

        String clientResponse = "";
        String serverResponse = "";
        String clientPayload = "";
        try {

            int clientState = 0;
            int i = 0;
            do {
                i++;

                clientResponse = cryptoClient.init(secureDataClient, clientKey, serverResponse);
                clientState = new JSONObject(clientResponse).getInt("state");
                clientPayload = new JSONObject(clientResponse).getString("payload");

                Logger.i("clientState: " + clientState);
                encStateValue.setText(clientState + "");
                // do not contact the server if it is in the last state
                // TODO non commentato per caricare crypto server in session server, solo li c'è init
                //if (clientState == 3) break;
                // connection

                // TODO CAMBIA NEI NUOVI METODI
                serverResponse = network.GetRequestSync("secureConnection?clientID=" + clientID + "&data=" + clientPayload);


//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("clientID", clientID);
//                jsonObject.put("data", clientPayload);
//
//                network.getRequest("secureConnection", jsonObject.toString(), cryptoClient, new NetworkCallback() {
//                    @Override
//                    public void onSuccess(String response) {
//                        if (ResponseParser.isStatusOK(response))
//                            callback.onSuccess(ResponseParser.parseMessage(response));
//                        else
//                            callback.onFailure(ResponseParser.parseMessage(response));
//
//                        serverResponse = response;
//                    }
//
//                    @Override
//                    public void onFailure(Exception e) {
//                        callback.onFailure(e.getMessage());
//                    }
//                });


                // contatta il server solo una volta
                if (clientState == 3) break;
                //todo prevedi reset nel caso si cancelli il file

            } while ((i < maxAttempts) /*&& (clientState != 3)*/);
        } catch (NetworkException e) {
            e.printStackTrace();
        }
        Logger.i("initSecureConnection done");
    }
}
