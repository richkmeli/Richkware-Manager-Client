package it.richkmeli.RMC.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.richkmeli.RMC.utils.Logger;
import it.richkmeli.RMC.utils.ResponseParser;
import it.richkmeli.jcrypto.Crypto;
import it.richkmeli.jcrypto.KeyExchangePayloadCompat;
import it.richkmeli.jcrypto.exception.CryptoException;
import okhttp3.*;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * Created by richk on 17/06/17.
 */
public class Network {

    private String url;
    private OkHttpClient client;
    private Headers lastHeaders;

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

    public String SendCommand(String ip, String port, String encryptionKey, boolean forceEncryption, String command) throws NetworkException {
        StringBuilder response = new StringBuilder();

        InetAddress receiverIP = null;
        try {
            receiverIP = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new NetworkException(e);
        }
        // client socket
        Socket talkSocket = null;

        PrintWriter talkBuffer;
        BufferedReader bufferedReader;

        try {
            talkSocket = new Socket();
            talkSocket.connect(new InetSocketAddress(receiverIP, Integer.parseInt(port)), 3000);
        } catch (IOException e) {
            throw new NetworkException(e);
            //throw new IOException("Failed to open socket on " + port);
        }

        try {

            talkBuffer =
                    new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            talkSocket.getOutputStream())), true);

            bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    talkSocket.getInputStream()));


            //boolean end = false;
            //while (!end) {

            bufferedReader.readLine(); // empty line
            String s = bufferedReader.readLine();

            if (s.compareTo("Encrypted Connection Established") == 0 || forceEncryption) {
                // send command
                command = Crypto.EncryptRC4(command, encryptionKey);
                talkBuffer.println(command);
                // receive response
                s = bufferedReader.readLine();
                s = Crypto.DecryptRC4(s, encryptionKey);

                while (s.compareTo("error: Malformed command") != 0) {
                    response.append(s).append("\n");
                    talkBuffer.println();
                    s = bufferedReader.readLine();
                    s = Crypto.DecryptRC4(s, encryptionKey);
                }
                // disconnection TODO: implement the execution of more command inside a connection
                command = Crypto.EncryptRC4("[[0]]", encryptionKey);
                talkBuffer.println(command);

            } else if (s.compareTo("Connection Established") == 0) {
                // send command
                talkBuffer.println(command);
                // receive response
                s = bufferedReader.readLine();
                while (s.compareTo("error: Malformed command") != 0) {
                    response.append(s).append("\n");
                    talkBuffer.println();
                    s = bufferedReader.readLine();
                }
                // disconnection TODO: implement the execution of more command inside a connection
                command = "[[0]]";
                talkBuffer.println(command);
            }

            bufferedReader.close();
            talkBuffer.close();
            talkSocket.close();

        } catch (IOException e) {
            try {
                talkSocket.close();
            } catch (IOException e1) {
                throw new NetworkException(e);
            }
            throw new NetworkException(e);
            //throw new IOException("Exception of communication buffer");
        }

        return response.toString();
    }


    public void setURL(String protocol, String server, String port, String service) throws NetworkException {
        try {
            this.url = String.valueOf(new URL(protocol + "://" + server + ":" + port + "/" + service + "/"));
        } catch (MalformedURLException e) {
            throw new NetworkException(e);
        }
    }


}

