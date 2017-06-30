package richk.RMC.controller;

import java.io.*;
import java.net.*;

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

   /* public String ConnectDevice(String ip, String port) {

    }

    public String DisconnectDevice(String ip, String port) {

    }*/

    public String SendCommand(String ip, String port, String command) throws NetworkException {
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
                talkBuffer.println(command);
                // receive response
                s = bufferedReader.readLine();
                while (s.compareTo("error: Malformed command") != 0) {
                    response.append(s).append("\n");
                    talkBuffer.println();
                    s = bufferedReader.readLine();
                }
                // disconnection TODO: implement the execution of more command inside a connection
                talkBuffer.println("[[0]]");
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
