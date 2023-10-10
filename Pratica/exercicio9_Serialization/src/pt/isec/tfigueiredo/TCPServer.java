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

                //Deserializar os bytes recebidos (objecto do tipo String)
                oout = new ObjectOutputStream(clientSocket.getOutputStream());
                oin = new ObjectInputStream(clientSocket.getInputStream());

                String received = (String) oin.readObject();

                System.out.println("Recebido \"" + received + "\"");

                if (!received.equalsIgnoreCase(TIME_REQUEST))
                    continue;

                Thread.sleep(15000);

                ObjectOutputStream bout = new ObjectOutputStream(clientSocket.getOutputStream());
                bout.writeObject(Calendar.getInstance());
                bout.flush();

                System.out.println("Enviado para cliente.");

                // Close the client socket
                clientSocket.close();
            }
        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo:\n\t" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Problema:\n\t" + e);
        } catch (SocketTimeoutException e) {
            System.out.println("Não foi recebida qualquer resposta:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro de acesso ao socket:\n\t" + e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
