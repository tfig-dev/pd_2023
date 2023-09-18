package exercicio5;

import java.net.*;
import java.io.*;

public class ServerGetDEMO {
    public static void main(String args[]) {
        try {
            System.out.println("Binding to local port 2000");
            // CREATE A DATAGRAM SOCKET, BOUND TO THE SPECIFIC PORT 2000
            DatagramSocket socket = new DatagramSocket(2000);
            // CREATE A DATAGRAM PACKET WITH A MAXIMUM BUFFER OF 256 BYTES
            DatagramPacket packet = new DatagramPacket(new byte[256], 256);
            // RECEIVE A PACKET (BY DEFAULT, THIS IS A BLOCKING OPERATION)
            socket.receive(packet);

            // DISPLAY PACKET INFORMATION
            InetAddress remote_addr = packet.getAddress();
            System.out.println("Sent by: " + remote_addr.getHostAddress());
            System.out.println("Sent from port: " + packet.getPort());
            // CREATE AND DISPLAY A STRING BASED ON PACKET CONTENTS

            String msg = new String(packet.getData(), 0, packet.getLength());
            System.out.println(msg);
            socket.close();
        } catch (IOException e) {
            System.err.println("Error - " + e);
        }
    }
}