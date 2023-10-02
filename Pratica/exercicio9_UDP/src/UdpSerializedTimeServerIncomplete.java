import java.net.*;
import java.io.*;
import java.util.*;

public class UdpSerializedTimeServerIncomplete {
    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";
      
    public static void main(String[] args) {

        DatagramPacket packet; //para receber os pedidos e enviar as respostas   
        
        ByteArrayInputStream bin;
        ObjectInputStream oin;
        
        ByteArrayOutputStream bout;
        ObjectOutputStream oout;
        
        String receivedMsg;
        Calendar calendar;      
                
        if(args.length != 1){
            System.out.println("Sintaxe: java UdpSerializedTimeServerIncomplete listeningPort");
            return;
        }
        
        try(DatagramSocket socket = new DatagramSocket(nteger.parseInt(args[0]))){
            
            System.out.println("UDP Time Server iniciado...");
            
            while(true){
            
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);
                
                //Deserializar os bytes recebidos (objecto do tipo String)
                -> bin
                -> oin                
                receivedMsg = ...

                System.out.println("Recebido \"" + receivedMsg + "\" de " + 
                    packet.getAddress().getHostAddress() + ":" + packet.getPort());

                if(!receivedMsg.equalsIgnoreCase(TIME_REQUEST)){
                    continue;
                }

                calendar = GregorianCalendar.getInstance();

                //Serializar o objecto calendar para bout
                -> bout
                -> oout
                oout.writeObject(...);
                
                packet.setData(bout...);
                packet.setLength(bout...);

                //O ip e porto de destino ja' se encontram definidos em packet
                socket.send(packet);

            }
                                    
        }catch(Exception e){
            System.out.println("Problema:\n\t"+e);
        }
    }
}
