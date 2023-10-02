import java.net.*;
import java.io.*;
import java.util.*;

public class UdpSerializedTimeServer {
    public static final int MAX_SIZE = 10000;
    public static final String TIME_REQUEST = "TIME";

    public static void main(String[] args) {

        DatagramPacket packet; //para receber os pedidos e enviar as respostas

        ByteArrayInputStream bin;
        ObjectInputStream oin;

        ByteArrayOutputStream bout;
        ObjectOutputStream oout;

        String received = null;
        Calendar calendar;

        if(args.length != 1){
            System.out.println("Sintaxe: java UdpSerializedTimeServerIncomplete listeningPort");
            return;
        }

        try(DatagramSocket socket = new DatagramSocket(Integer.parseInt(args[0]))){
            System.out.println("UDP Time Server iniciado...");

            while(true){
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                //Deserializar os bytes recebidos (objecto do tipo String)
                bin = new ByteArrayInputStream(packet.getData());
                oin = new ObjectInputStream(bin);

                received = (String) oin.readObject();

                System.out.println("Recebido \"" + received + "\" de " +
                        packet.getAddress().getHostAddress() + ":" + packet.getPort());

                if(!received.equalsIgnoreCase(TIME_REQUEST)){
                    continue;
                }

                calendar = GregorianCalendar.getInstance();

                //Serializar o objecto calendar para bout
                bout = new ByteArrayOutputStream();
                bout.flush();
                oout = new ObjectOutputStream(bout);
                oout.flush();
                oout.writeObject(calendar);
                packet.setData(bout.toByteArray(),0,bout.toByteArray().length);
                System.out.println("Enviado para:" + packet.getAddress() + ":" + packet.getPort());
                //O ip e porto de destino ja' se encontram definidos em packet
                socket.send(packet);

            }

        }catch(Exception e){
            System.out.println("Problema:\n\t"+e);
        }
    }
}