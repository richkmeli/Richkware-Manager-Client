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

        return outString.toString();
    }
}
