package pt.isec.tfigueiredo;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; // seconds

    public static void main(String[] args) {
        InetAddress serverAddr = null;
        int serverPort = -1;

        ObjectOutputStream oout;
        ObjectInputStream oin;

        Calendar response;

        if (args.length != 2) {
            System.out.println("Sintaxe: java TcpSerializedTimeClient serverAddress serverTcpPort");
            return;
        }

        try (Socket socket = new Socket(serverAddr, serverPort)){
            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            socket.setSoTimeout(TIMEOUT * 1000);

            // Create output stream
            oout = new ObjectOutputStream(socket.getOutputStream());

            // Create input stream
            oin = new ObjectInputStream(socket.getInputStream());

            // Send the TIME_REQUEST to the server
            oout.writeObject(TIME_REQUEST);
            oout.flush();

            // Receive the calendar object from the server
            response = (Calendar) oin.readObject();
            System.out.println("Hora indicada pelo servidor: " + response.getTime());
        } catch (Exception e) {
            System.out.println("Problema:\n\t" + e);
        }
    }
}