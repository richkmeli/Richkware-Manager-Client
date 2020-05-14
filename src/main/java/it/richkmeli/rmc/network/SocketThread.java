package it.richkmeli.rmc.network;

import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.jframework.util.log.Logger;
import it.richkmeli.rmc.swing.RichkwareCallback;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketThread extends Thread {

    private String ip;
    private String port;
    private String encryptionKey;
    private boolean forceEncryption;
    private SocketCallback callback;
    private Socket talkSocket;

    private PrintWriter outBuffer;
    private BufferedReader inBuffer;

    private volatile boolean open;

    public SocketThread(String ip, String port, String encryptionKey, boolean forceEncryption, SocketCallback callback) {
        this.ip = ip;
        this.port = port;
        this.encryptionKey = encryptionKey;
        this.forceEncryption = forceEncryption;
        this.callback = callback;

        open = true;
    }

    @Override
    public void run() {
        super.run();
        try {
            openSocket();
            while (open) ;
        } catch (SocketException e) {
            Logger.error(e.getMessage());
            callback.onFailure(e);
        }
    }

    private void openSocket() throws SocketException {
        InetAddress receiverIP = null;
        try {
            receiverIP = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new SocketException(e);
        }
        talkSocket = null;
        try {
            talkSocket = new Socket();
            talkSocket.connect(new InetSocketAddress(receiverIP, Integer.parseInt(port)), 15000);

            outBuffer =
                    new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            talkSocket.getOutputStream())), true);

            inBuffer =
                    new BufferedReader(
                            new InputStreamReader(
                                    talkSocket.getInputStream()));


            //boolean end = false;
            //while (!end) {

            inBuffer.readLine(); // empty line
            String s = inBuffer.readLine();

            if (s.compareTo("Encrypted Connection Established") == 0 || forceEncryption) {
                this.forceEncryption = true;
                callback.onSuccess(this);
            } else if (s.compareTo("Connection Established") == 0) {
                callback.onSuccess(this);
            } else {
                callback.onFailure(new SocketException(new Exception(s)));
            }
        } catch (IOException e) {
            throw new SocketException(e);
        }
    }

    public void sendCommand(String command, RichkwareCallback callback) {
        command = "[[1]]" + command;
        StringBuilder response = new StringBuilder();
        try {
            String s;
            if (forceEncryption) {
                Logger.info("I'm in SockerThread, sendCommand. Encryption.");
                // send command
                command = Crypto.encryptRC4(command, encryptionKey);
                outBuffer.println(command);
                // receive response
                s = inBuffer.readLine();
                s = Crypto.decryptRC4(s, encryptionKey);

                while (s.compareTo("error: Malformed command") != 0) {
                    response.append(s).append("\n");
                    outBuffer.println();
                    s = inBuffer.readLine();
                    s = Crypto.decryptRC4(s, encryptionKey);
                }
//                // disconnection
//                command = Crypto.EncryptRC4("[[0]]", encryptionKey);
//                outBuffer.println(command);
            } else {
                Logger.info("I'm in SockerThread, sendCommand. No encryption.");
                // send command
                outBuffer.println(command);
                // receive response
                s = inBuffer.readLine();
                Logger.info("s: " + s);
                while (s.compareTo("error: Malformed command") != 0) {
                    response.append(s).append("\n");
                    outBuffer.println();
                    s = inBuffer.readLine();
                    Logger.info("s: " + s);
                }
//                // disconnection
//                command = "[[0]]";
//                outBuffer.println(command);
            }
        } catch (IOException | CryptoException e) {
            callback.onFailure(e.getMessage());
        }
        callback.onSuccess(response.toString());
    }

    public void disconnect() {
        if (forceEncryption) {
            // disconnection
            outBuffer.println(Crypto.encryptRC4("[[0]]", encryptionKey));
        } else {
            // disconnection
            outBuffer.println("[[0]]");
        }
        open = false;
    }

}