package it.richkmeli.RMC.utils;

public class Logger {
    public static final Boolean DEBUG = true;

    public static void i(String message) {
        if (DEBUG) {
            System.out.println(getTag() + " - " + message);
        }

    }

    public static void e(String message) {
        if (DEBUG) {
            System.err.println(getTag() + " - " + message);
        }

    }

    public static void e(String message, Throwable throwable) {
        if (DEBUG) {
            System.err.println(getTag() + " - " + message + " || " + throwable.getMessage());
        }
    }

    private static String getTag() {
        String className = (new Exception()).getStackTrace()[2].getClassName();
        return "RMC::" + className.substring(1 + className.lastIndexOf(46));
    }
}