package exercicio5;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientSendDEMO {
    //para funcionar, precisamos de usar o terminal
    // java ClientSendDEMO.java 10.65.129.159 6002
    // se usarmos o nosso server:
    // java ClientSendDEMO.java 127.0.0.1 2000
    // onde:  arg0 -> hostname e arg1 -> port

    public static void main(String args[]) {
        // CHECK FOR VALID NUMBER OF PARAMETERS
        int argc = args.length;
        if (argc != 2) {
            System.out.println("Syntax :");
            System.out.print("java ClientSendDEMO args error");
            return;
        }

        String hostname = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String message = "SCP";

        try {
            System.out.println("Binding to a local port");
            // CREATE A DATAGRAM SOCKET, BOUND TO ANY AVAILABLE LOCAL PORT
            DatagramSocket socket = new DatagramSocket();
            System.out.println("Bound to local port " + socket.getLocalPort());

            // CREATE A MESSAGE TO SEND USING A UDP PACKET (ARRAY OF BYTES)
            byte[] barray = message.getBytes();

            // CREATE A DATAGRAM PACKET, CONTAINING OUR BYTE ARRAY
            DatagramPacket packet = new DatagramPacket(barray, barray.length);
            System.out.println("Looking up hostname " + hostname);
            // LOOKUP THE SPECIFIED HOSTNAME, AND GET AN INETADDRESS
            InetAddress addr = InetAddress.getByName(hostname);
            System.out.println("Hostname resolved as " + addr.getHostAddress());

            // ADDRESS PACKET TO SENDER
            packet.setAddress(addr);

            // SET PORT NUMBER TO 2000
            packet.setPort(serverPort);

            // SEND THE PACKET - REMEMBER NO GUARANTEE OF DELIVERY
            socket.send(packet);
            System.out.println("Packet sent!");
        } catch (UnknownHostException e) {
            System.err.println("Can't find host " + hostname);
        } catch (IOException e) {
            System.err.println("Error - " + e);
        }
    }
}