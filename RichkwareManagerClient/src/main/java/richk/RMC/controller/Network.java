package richk.RMC.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by richk on 17/06/17.
 */
public class Network {

    public String GetURLContents(String sUrl) {
        StringBuilder outString = new StringBuilder();

        URL url = null;
        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        URLConnection yc = null;
        BufferedReader in = null;

        try {
            yc = url.openConnection();
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                outString.append(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outString.toString();
    }
}
