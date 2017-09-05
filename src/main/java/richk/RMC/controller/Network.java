package richk.RMC.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import richk.RMC.util.Crypto;
import richk.RMC.util.CryptoException;
import richk.RMC.util.KeyExchangePayload;

import javax.crypto.SecretKey;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by richk on 17/06/17.
 */
public class Network {

    public String GetURLContents(String sUrl) throws NetworkException {
        StringBuilder outString = new StringBuilder();

        URL url = null;
        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            throw new NetworkException(e);
        }

        URLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        try {
            urlConnection = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null)
                outString.append(inputLine);
            bufferedReader.close();
        } catch (IOException e) {
            throw new NetworkException(e);
        }

        String out = outString.toString();
        return out;
    }

    public String GetEncryptedURLContents(String sUrl) throws NetworkException {
        String out = null;
        try {
            KeyPair keyPair = Crypto.GetGeneratedKeyPairRSA();
            PublicKey RSApublicKeyClient = keyPair.getPublic();
            PrivateKey RSAprivateKeyClient = keyPair.getPrivate();

            // URL editing: appending to the URL a GET parameter (HTTP), to enable encryption server-side.
            String url = null;
            try {
                url = sUrl + "?encryption=true&Kpub=" + Crypto.savePublicKey(RSApublicKeyClient);
            } catch (GeneralSecurityException e) {
                throw new NetworkException(e);
            }

            out = GetURLContents(url);
            Type listType = new TypeToken<KeyExchangePayload>() {
            }.getType();
            Gson gson = new Gson();
            KeyExchangePayload keyExchangePayload = gson.fromJson(out, listType);

            SecretKey AESsecretKey = Crypto.GetAESKeyFromKeyExchange(keyExchangePayload, RSAprivateKeyClient);
            String data = keyExchangePayload.getData();

            out = Crypto.DecryptAES(data, AESsecretKey);
        } catch (CryptoException e) {
            throw new NetworkException(e);
        }

        return out;
    }

   /* public String ConnectDevice(String ip, String port) {

    }

    public String DisconnectDevice(String ip, String port) {

    }*/

    public String SendCommand(String ip, String port,String encryptionKey, String command) throws NetworkException {
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

            if (s.compareTo("Connection Established") == 0) {
                // send command
                if (encryptionKey != null) command = Crypto.EncryptRC4(command,encryptionKey);

                talkBuffer.println(command);

                // receive response
                s = bufferedReader.readLine();
                if (encryptionKey != null) s = Crypto.DecryptRC4(s,encryptionKey);

                while (s.compareTo("error: Malformed command") != 0) {
                    response.append(s).append("\n");
                    talkBuffer.println();
                    s = bufferedReader.readLine();
                    if (encryptionKey != null) s = Crypto.DecryptRC4(s,encryptionKey);
                }
                // disconnection TODO: implement the execution of more command inside a connection
                command = "[[0]]";
                if (encryptionKey != null) command = Crypto.EncryptRC4(command,encryptionKey);
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

}

