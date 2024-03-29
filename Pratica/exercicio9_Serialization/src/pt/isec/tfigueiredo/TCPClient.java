package pt.isec.tfigueiredo;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; // seconds

    public static void main(String[] args) throws UnknownHostException {
        InetAddress serverAddr = null;
        int serverPort = -1;

        Calendar response;

        if (args.length != 2) {
            System.out.println("Sintaxe: java TcpSerializedTimeClient serverAddress serverTcpPort");
            return;
        }

        serverAddr = InetAddress.getByName(args[0]);
        serverPort = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(serverAddr, serverPort)) {

            socket.setSoTimeout(TIMEOUT * 1000);

            ObjectOutputStream bout = new ObjectOutputStream(socket.getOutputStream());
            bout.writeObject(TIME_REQUEST);
            bout.flush();

            ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            response = (Calendar) oin.readObject();

            System.out.println("Hora indicada pelo servidor: " + response.get(GregorianCalendar.HOUR_OF_DAY) + ":" +
                    response.get(GregorianCalendar.MINUTE) + ":" + response.get(GregorianCalendar.SECOND));
        } catch (ClassNotFoundException e) {
            System.out.println("Classe não encontrada:\n\t" + e);
        } catch (Exception e) {
            System.out.println("Problema:\n\t" + e);
        }
    }
}