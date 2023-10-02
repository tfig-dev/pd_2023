package pt.isec.tfigueiredo;

import java.net.*;
import java.io.*;
import java.util.*;

public class UDPClient {
    private SerialInfo sers;
    public static final int MAX_SIZE = 10000;
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args) {

//        sers = new pt.isec.tfigueiredo.SerialInfo();
//        sers.openSerialFile();

        InetAddress serverAddr = null;
        int serverPort = -1;

        ByteArrayOutputStream bout;
        ObjectOutputStream oout;

        ByteArrayInputStream bin;
        ObjectInputStream oin;

        DatagramPacket packet = null;
        Calendar response;

        if(args.length != 2){
            System.out.println("Sintaxe: java UdpSerializedTimeClientIncomplete serverAddress serverUdpPort");
            return;
        }

        try(DatagramSocket socket = new DatagramSocket()){

            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            socket.setSoTimeout(TIMEOUT*1000);

            //Serializar a String TIME para um array de bytes encapsulado por bout
            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);
            oout.writeObject(TIME_REQUEST);
            
            //Construir um datagrama UDP com o resultado da serializa��o
            packet = new DatagramPacket(bout.toByteArray(), bout.size(), serverAddr, serverPort);

            socket.send(packet);

            packet = new DatagramPacket(new byte[MAX_SIZE],MAX_SIZE);
            socket.receive(packet);

            //Deserializar o fluxo de bytes recebido para um array de bytes encapsulado por bin
            oin = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
            response = (Calendar) oin.readObject();
            System.out.println("Hora indicada pelo servidor: " + response.getTime());
        }catch(Exception e){
            System.out.println("Problema:\n\t"+e);
        }
    }

}