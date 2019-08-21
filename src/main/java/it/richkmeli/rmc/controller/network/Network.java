package it.richkmeli.rmc.controller.network;

import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.rmc.controller.NetworkCallback;
import it.richkmeli.rmc.utils.Logger;
import it.richkmeli.rmc.utils.ResponseParser;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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

    public void deleteSession() {
        lastHeaders = null;
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
                parameters.append("&").append(key).append("=").append(jsonParameters.get(key));
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

            jsonParametersString = jsonParametersString == null ? "" : jsonParametersString;
            //String encryptedParameters = cryptoClient.encrypt(params);
            // when encryption is enabled they are passed as JSON
            Logger.i("Get request to: (decrypted) " + url + " :\"" + jsonParametersString + "\"");
            String encryptedParameters = null;
            try {
                encryptedParameters = cryptoClient.encrypt(jsonParametersString);
            } catch (CryptoException e) {
                callback.onFailure(e);
            }

            Logger.i("Get request to:  (encrypted) " + url + " :\"" + encryptedParameters + "\"");
            parameters = new StringBuilder("?channel=rmc&data=" + encryptedParameters);
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

                if (ResponseParser.parseStatus(jsonResponse).equalsIgnoreCase("ok")) {

                    if (cryptoClient != null) {
                        Logger.i("Get response (encrypted): " + jsonResponse);

                        String messageResponse = ResponseParser.parseMessage(jsonResponse);

                        try {
                            messageResponse = cryptoClient.decrypt(messageResponse);
                        } catch (CryptoException e) {
                            callback.onFailure(e);
                        }

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
                } else {
                    Logger.i("Get response: " + jsonResponse);

                    callback.onFailure(new Exception(ResponseParser.parseMessage(jsonResponse)));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

    public void getRequestCompat(String servlet, String jsonParametersString, NetworkCallback callback) {
        StringBuilder parameters = new StringBuilder("?");
        if (jsonParametersString != null && !jsonParametersString.isEmpty()) {
            JSONObject jsonParameters = new JSONObject(jsonParametersString);
            for (String key : jsonParameters.keySet()) {
                parameters.append("&").append(key).append("=").append(jsonParameters.get(key));
            }
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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string().trim();
                if (response.headers().get("Set-Cookie") != null)
                    lastHeaders = response.headers();

                Logger.i("Get response: " + jsonResponse);

                callback.onSuccess(jsonResponse);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

//    public void getRequestCompat(String servlet, String jsonParametersString, boolean encryption, NetworkCallback callback) {
//        StringBuilder parameters = new StringBuilder("?");
//        if (jsonParametersString != null && !jsonParametersString.isEmpty()) {
//            JSONObject jsonParameters = new JSONObject(jsonParametersString);
//            for (String key : jsonParameters.keySet()) {
//                parameters.append("&").append(key).append("=").append(jsonParameters.get(key));
//            }
//        }
//
//        PrivateKey RSAprivateKeyClient = null;
//        try {
//            if (encryption) {
//                KeyPair keyPair = CryptoCompat.getGeneratedKeyPairRSA();
//                PublicKey RSApublicKeyClient = keyPair.getPublic();
//                RSAprivateKeyClient = keyPair.getPrivate();
//
//                parameters.append("?&encryption=true&Kpub=").append(CryptoCompat.savePublicKey(RSApublicKeyClient));
//            }
//        } catch (GeneralSecurityException | CryptoException e) {
//            callback.onFailure(new NetworkException(e));
//        }
//
//        URL url = null;
//        try {
//            url = new URL(this.url + servlet + parameters);
//        } catch (MalformedURLException e) {
//            callback.onFailure(new NetworkException(e));
//        }
//
//        Request request;
//
//        Logger.i("Get request to: " + url);
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
//        PrivateKey finalRSAprivateKeyClient = RSAprivateKeyClient;
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String jsonResponse = response.body().string().trim();
//                if (response.headers().get("Set-Cookie") != null)
//                    lastHeaders = response.headers();
//
//                try {
//
//                    if (encryption) {
//                        Logger.i("Get response (encrypted): " + jsonResponse);
//
//                        String messageResponse = ResponseParser.parseMessage(jsonResponse);
//
//                        Type listType = new TypeToken<KeyExchangePayloadCompat>() {
//                        }.getType();
//                        Gson gson = new Gson();
//                        KeyExchangePayloadCompat keyExchangePayload = gson.fromJson(messageResponse, listType);
//
//                        SecretKey AESsecretKey = CryptoCompat.getAESKeyFromKeyExchange(keyExchangePayload, finalRSAprivateKeyClient);
//                        String data = keyExchangePayload.getData();
//
//                        messageResponse = CryptoCompat.decryptRC4(data, new String(AESsecretKey.getEncoded()));
//
//                        //CREATE new JSON
//                        JSONObject json = new JSONObject(jsonResponse);
//                        json.remove("message");
//                        json.put("message", messageResponse);
//                        jsonResponse = json.toString();
//
//                        Logger.i("Get response (decrypted): " + jsonResponse);
//
//                        callback.onSuccess(jsonResponse);
//                    } else {
//                        Logger.i("Get response: " + jsonResponse);
//
//                        callback.onSuccess(jsonResponse);
//                    }
//                } catch (CryptoException e) {
//                    Logger.e(e.getMessage());
//                    callback.onFailure(new NetworkException(e));
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                callback.onFailure(new NetworkException(e));
//            }
//        });
//    }

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

