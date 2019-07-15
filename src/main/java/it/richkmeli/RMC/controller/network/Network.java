package it.richkmeli.RMC.controller.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.RMC.controller.NetworkCallback;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;
import it.richkmeli.jcrypto.Crypto;
import it.richkmeli.jcrypto.CryptoCompat;
import it.richkmeli.jcrypto.KeyExchangePayloadCompat;
import it.richkmeli.jcrypto.exception.CryptoException;
import okhttp3.*;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

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

    public void getRequest(String servlet, String jsonParametersString, Crypto.Client cryptoClient, NetworkCallback callback) {
        StringBuilder parameters = new StringBuilder("?");
        if (jsonParametersString != null && !jsonParametersString.isEmpty()) {
            JSONObject jsonParameters = new JSONObject(jsonParametersString);
            for (String key : jsonParameters.keySet()) {
                parameters.append("&").append(key + "=" + jsonParameters.get(key));
            }
        }

        URL url = null;

        if (cryptoClient != null) {
            String params = parameters.toString();
            try {
                url = new URL(this.url + servlet + params);
            } catch (MalformedURLException e) {
                callback.onFailure(new NetworkException(e));
            }
            Logger.i("Get request to: " + url + " :\"" + cryptoClient.encrypt(params) + "\"");
            parameters = new StringBuilder("?encryption=true&data=" + cryptoClient.encrypt(params));
        }

//        URL url = null;
        try {
            url = new URL(this.url + servlet + parameters);
        } catch (MalformedURLException e) {
            callback.onFailure(new NetworkException(e));
        }

        Request request;

        Logger.i("Get request to: " + url);

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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string().trim();
                if (response.headers().get("Set-Cookie") != null)
                    lastHeaders = response.headers();

                if (cryptoClient != null) {
                    Logger.i("Get response (encrypted): " + jsonResponse);

                    String messageResponse = ResponseParser.parseMessage(jsonResponse);

                    //TODO boolean decryptResponse param
//                        if(decryptResponse){
                    messageResponse = cryptoClient.decrypt(messageResponse);
//                        }

                    //CREATE new JSON
                    JSONObject json = new JSONObject(jsonResponse);
                    json.remove("message");
                    json.put("message", messageResponse);
                    jsonResponse = json.toString();

                    Logger.i("Get response (decrypted): " + jsonResponse);

                    callback.onSuccess(jsonResponse);
                } else {
                    Logger.i("Get response: " + jsonResponse);

                    callback.onSuccess(jsonResponse);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

    public void getRequestCompat(String servlet, String jsonParametersString, boolean encryption, NetworkCallback callback) {
        StringBuilder parameters = new StringBuilder("?");
        if (jsonParametersString != null && !jsonParametersString.isEmpty()) {
            JSONObject jsonParameters = new JSONObject(jsonParametersString);
            for (String key : jsonParameters.keySet()) {
                parameters.append("&").append(key + "=" + jsonParameters.get(key));
            }
        }

        PrivateKey RSAprivateKeyClient = null;
        try {
            if (encryption) {
                KeyPair keyPair = CryptoCompat.getGeneratedKeyPairRSA();
                PublicKey RSApublicKeyClient = keyPair.getPublic();
                RSAprivateKeyClient = keyPair.getPrivate();

                parameters.append("?&encryption=true&Kpub=" + CryptoCompat.savePublicKey(RSApublicKeyClient));
            }
        } catch (GeneralSecurityException | CryptoException e) {
            callback.onFailure(new NetworkException(e));
        }

        URL url = null;
        try {
            url = new URL(this.url + servlet + parameters);
        } catch (MalformedURLException e) {
            callback.onFailure(new NetworkException(e));
        }

        Request request;

        Logger.i("Get request to: " + url);

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

        PrivateKey finalRSAprivateKeyClient = RSAprivateKeyClient;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string().trim();
                if (response.headers().get("Set-Cookie") != null)
                    lastHeaders = response.headers();

                try {

                    if (encryption) {
                        Logger.i("Get response (encrypted): " + jsonResponse);

                        String messageResponse = ResponseParser.parseMessage(jsonResponse);

                        Type listType = new TypeToken<KeyExchangePayloadCompat>() {
                        }.getType();
                        Gson gson = new Gson();
                        KeyExchangePayloadCompat keyExchangePayload = gson.fromJson(messageResponse, listType);

                        SecretKey AESsecretKey = CryptoCompat.getAESKeyFromKeyExchange(keyExchangePayload, finalRSAprivateKeyClient);
                        String data = keyExchangePayload.getData();

                        messageResponse = CryptoCompat.decryptRC4(data, new String(AESsecretKey.getEncoded()));

                        //CREATE new JSON
                        JSONObject json = new JSONObject(jsonResponse);
                        json.remove("message");
                        json.put("message", messageResponse);
                        jsonResponse = json.toString();

                        Logger.i("Get response (decrypted): " + jsonResponse);

                        callback.onSuccess(jsonResponse);
                    } else {
                        Logger.i("Get response: " + jsonResponse);

                        callback.onSuccess(jsonResponse);
                    }
                } catch (CryptoException e) {
                    Logger.e(e.getMessage());
                    callback.onFailure(new NetworkException(e));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

    public void putRequest(String servlet, String jsonParamentesString, Crypto.Client cryptoClient, NetworkCallback callback) {

        JSONObject jsonParameters = new JSONObject(jsonParamentesString);

        if (cryptoClient != null) {
            //TODO ENCRYPT
        }

        URL url = null;
        try {
            url = new URL(this.url + servlet);
        } catch (MalformedURLException e) {
            callback.onFailure(new NetworkException(e));
        }

        Request request;

        Logger.i("Put request to: " + url);
        Logger.i("Put body json: " + jsonParameters.toString());

        RequestBody body = RequestBody.create(JSON, jsonParameters.toString());


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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string().trim();
                if (response.headers().get("Set-Cookie") != null)
                    lastHeaders = response.headers();

                if (cryptoClient != null) {
                    Logger.i("Put response (encrypted): " + jsonResponse);

                    //TODO DECRYPT

                    Logger.i("Get response (decrypted): " + jsonResponse);

                    callback.onSuccess(jsonResponse);
                } else {
                    Logger.i("Get response: " + jsonResponse);

                    callback.onSuccess(jsonResponse);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

    public String GetRequestSync(String parameter) throws NetworkException {
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
}

