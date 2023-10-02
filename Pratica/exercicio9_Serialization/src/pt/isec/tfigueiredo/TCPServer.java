package pt.isec.tfigueiredo;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
    public static final String TIME_REQUEST = "TIME";

    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        ObjectOutputStream oout = null;
        ObjectInputStream oin = null;
        Calendar calendar;

        if (args.length != 1) {
            System.out.println("Sintaxe: java TcpSerializedTimeServer listeningPort");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Time Server iniciado...");

            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                // Create output stream
                oout = new ObjectOutputStream(clientSocket.getOutputStream());

                // Create input stream
                oin = new ObjectInputStream(clientSocket.getInputStream());

                String received = (String) oin.readObject();

                System.out.println("Recebido \"" + received + "\"");

                if (!received.equalsIgnoreCase(TIME_REQUEST)) {
                    continue;
                }

                calendar = GregorianCalendar.getInstance();

                // Send the calendar object to the client
                oout.writeObject(calendar);
                oout.flush();
                System.out.println("Enviado para cliente.");

                // Close the client socket
                clientSocket.close();
            }
        } catch (Exception e) {
            System.out.println("Problema:\n\t" + e);
        } finally {
            try {
                if (oout != null) oout.close();
                if (oin != null) oin.close();
                if (clientSocket != null) clientSocket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
