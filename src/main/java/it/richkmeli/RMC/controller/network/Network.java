package it.richkmeli.RMC.controller.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.RMC.model.Device;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;
import it.richkmeli.jcrypto.Crypto;
import it.richkmeli.jcrypto.KeyExchangePayloadCompat;
import it.richkmeli.jcrypto.exception.CryptoException;
import okhttp3.*;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Created by richk on 17/06/17.
 */
public class Network {

    private String url;
    private OkHttpClient client;
    private Headers lastHeaders;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Network() {
        client = new OkHttpClient();
        lastHeaders = null;
    }

    public String GetURLContents(String parameter) throws NetworkException {
        URL url = null;
        try {
            url = new URL(this.url + parameter);
        } catch (MalformedURLException e) {
            throw new NetworkException(e);
        }

        Response response;
        Request request;

        Logger.i("Request to: " + url);

        if (lastHeaders != null)
            request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", lastHeaders.get("Set-Cookie"))
                    .get()
                    .build();
        else
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new NetworkException(e);
        }

        String responseString = null;
        try {
            responseString = response.body().string().trim();
        } catch (IOException e) {
            throw new NetworkException(e);
        }

        Logger.i(responseString);

        if (response.headers().get("Set-Cookie") != null)
            lastHeaders = response.headers();

        return responseString;
    }

//    public String GetURLContents(String sUrl) throws NetworkException {
//        StringBuilder outString = new StringBuilder();
//
//        URL url = null;
//        try {
//            url = new URL(sUrl);
//        } catch (MalformedURLException e) {
//            throw new NetworkException(e);
//        }
//
//        URLConnection urlConnection = null;
//        BufferedReader bufferedReader = null;
//
//        try {
//            urlConnection = url.openConnection();
//            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//            String inputLine;
//            while ((inputLine = bufferedReader.readLine()) != null)
//                outString.append(inputLine);
//            bufferedReader.close();
//        } catch (IOException e) {
//            throw new NetworkException(e);
//        }
//
//        String out = outString.toString();
//        return out;
//    }

    public String GetEncryptedURLContents(String parameter) throws NetworkException {
        String response = null;
        String out = null;
        try {
            KeyPair keyPair = Crypto.GetGeneratedKeyPairRSA();
            PublicKey RSApublicKeyClient = keyPair.getPublic();
            PrivateKey RSAprivateKeyClient = keyPair.getPrivate();

            // URL editing: appending to the URL a GET parameter (HTTP), to enable encryption server-side.
            String parameterEncryption = parameter;
            try {
                parameterEncryption = parameter + "?&encryption=true&Kpub=" + Crypto.savePublicKey(RSApublicKeyClient);
            } catch (GeneralSecurityException e) {
                throw new NetworkException(e);
            }

            response = GetURLContents(parameterEncryption);

            String messageResponse = ResponseParser.parseMessage(response);

            Type listType = new TypeToken<KeyExchangePayloadCompat>() {
            }.getType();
            Gson gson = new Gson();
            KeyExchangePayloadCompat keyExchangePayload = gson.fromJson(messageResponse, listType);

            SecretKey AESsecretKey = Crypto.GetAESKeyFromKeyExchange(keyExchangePayload, RSAprivateKeyClient);
            String data = keyExchangePayload.getData();

            messageResponse = Crypto.DecryptAES(data, AESsecretKey);

            //CREATE new JSON
            JSONObject json = new JSONObject(response);
            json.remove("message");
            json.put("message", messageResponse);
            out = json.toString();
        } catch (CryptoException e) {
            throw new NetworkException(e);
        }

        return out;
    }

   /* public String ConnectDevice(String ip, String port) {

    }

    public String DisconnectDevice(String ip, String port) {

    }*/

    public void setURL(String protocol, String server, String port, String service) throws NetworkException {
        try {
            this.url = String.valueOf(new URL(protocol + "://" + server + ":" + port + "/" + service + "/"));
        } catch (MalformedURLException e) {
            throw new NetworkException(e);
        }
    }

    public void openSocket(String ip, String port, String encryptionKey, boolean forceEncryption, SocketCallback callback) {
        SocketThread socketThread = new SocketThread(ip, port, encryptionKey, forceEncryption, callback);
        socketThread.start();
    }

    public void reverseCommand(Device device, String commands) throws NetworkException {

        //TODO SPOSTARE DOVE
        String[] commandsArray = commands.split("\n");
        String finalString = "";
        byte[][] base64Array = new byte[commandsArray.length][];
        for (int i = 0; i < commandsArray.length; i++) {
            finalString += (Base64.getEncoder().encodeToString(commandsArray[i].getBytes()));
            if (i != commandsArray.length - 1)
                finalString += "##";
        }
        finalString = Base64.getEncoder().encodeToString(finalString.getBytes());

        URL url = null;
        try {
            url = new URL(this.url + "command"); // ?device="+ device.getName() + "&commands='" + finalString
        } catch (MalformedURLException e) {
            throw new NetworkException(e);
        }


        Response response;
        Request request;

        JSONObject jsonRequest = new JSONObject()
                .put("device", device.getName())
                .put("commands", finalString);

        Logger.i("JSON: " + jsonRequest.toString());
        RequestBody body = RequestBody.create(JSON, jsonRequest.toString());


        if (lastHeaders != null)
            request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", lastHeaders.get("Set-Cookie"))
                    .put(body)
                    .build();
        else
            request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new NetworkException(e);
        }

        String responseString = null;
        try {
            responseString = response.body().string().trim();
        } catch (IOException e) {
            throw new NetworkException(e);
        }

        Logger.i(responseString);

        if (response.headers().get("Set-Cookie") != null)
            lastHeaders = response.headers();

    }

//    public String getRequest(String servlet, String jsonParametersString) {
//
//        String parameters = "";
//        if(jsonParametersString!=null || !jsonParametersString.isEmpty()){
//            JSONObject jsonParameters = new JSONObject(jsonParametersString);
//            for(String key : jsonParameters.keySet()){
//                parameters+="&?
//            }
//        }
//
//        URL url = null;
//        try {
//            url = new URL(this.url);
//        } catch (MalformedURLException e) {
//            throw new NetworkException(e);
//        }
//
//        Response response;
//        Request request;
//
//        Logger.i("Get equest to: " + url);
//
//        if (lastHeaders != null)
//            request = new Request.Builder()
//                    .url(url)
//                    .addHeader("Cookie", lastHeaders.get("Set-Cookie"))
//                    .get()
//                    .build();
//        else
//            request = new Request.Builder()
//                    .url(url)
//                    .get()
//                    .build();
//
//        try {
//            response = client.newCall(request).execute();
//        } catch (IOException e) {
//            throw new NetworkException(e);
//        }
//
//        String responseString = null;
//        try {
//            responseString = response.body().string().trim();
//        } catch (IOException e) {
//            throw new NetworkException(e);
//        }
//
//        Logger.i(responseString);
//
//        if (response.headers().get("Set-Cookie") != null)
//            lastHeaders = response.headers();
//
//        return responseString;
//    }

}

