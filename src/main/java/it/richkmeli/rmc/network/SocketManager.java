package it.richkmeli.rmc.network;

public class SocketManager {
    public static void openSocket(String ip, String port, String encryptionKey, boolean forceEncryption, SocketCallback callback) {
        SocketThread socketThread = new SocketThread(ip, port, encryptionKey, forceEncryption, callback);
        socketThread.start();
    }
}
