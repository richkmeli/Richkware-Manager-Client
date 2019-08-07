package it.richkmeli.rmc.utils;

import it.richkmeli.jframework.crypto.Crypto;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utils {

    private static String getHostName() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return ip.getCanonicalHostName();
        } catch (UnknownHostException e) {
            return "undefined";
        }

    }

    private static String getHostUsername() {
        return System.getProperty("user.name");
    }

    private static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    private static String getOsArch() {
        return System.getProperty("os.arch");
    }

    private static String getOsName() {
        return System.getProperty("os.name");
    }

    private static String getOsVersion() {
        return System.getProperty("os.version");
    }

    public static String getDeviceInfo() {
        return getOsArch() + "_" + getOsName() + "_" + getOsVersion() + "_" + getHostName() + getFileSeparator() + getHostUsername();
    }

    public static String getDeviceIdentifier() {
        String device = getHostName() + getFileSeparator() + getHostUsername();
        Logger.i("DeviceIdentifier: " + device);
        return Crypto.hash(device);
    }

}
