package richk.RMC.model;

public class Device {
    private String name;
    private String IP;
    private String serverPort;
    private String lastConnection;
    private String encryptionKey;

    public Device(String name, String iP, String serverPort, String lastConnection, String encryptionKey) {
        super();
        this.name = name;
        IP = iP;
        this.serverPort = serverPort;
        this.lastConnection = lastConnection;
        this.encryptionKey = encryptionKey;
    }

    public String getIP() {
        return IP;
    }

    public String getLastConnection() {
        return lastConnection;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getName() {
        return name;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public void setIP(String iP) {
        IP = iP;
    }

    public void setLastConnection(String lastConnection) {
        this.lastConnection = lastConnection;
    }

    public void setName(String name) {
        this.name = name;
    }

}
