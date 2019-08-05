package it.richkmeli.rmc.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseParser {

    public static String parseStatus(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.getString("status");
    }

    public static boolean isStatusOK(String jsonResponse) throws JSONException {
        return parseStatus(jsonResponse).equalsIgnoreCase("ok");
    }

    public static int parseStatusCode(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.getInt("statusCode");
    }

    public static String parseMessage(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.getString("message");
    }
}
