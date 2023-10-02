
import java.util.*;
import java.net.*;
import java.io.*;

public class TcpSerializedTimeServerIncomplete {

    public static final String TIME_REQUEST = "TIME";
    
    public static void main(String args[]){
        
        String request;
        Calendar calendar;     
        
        if(args.length != 1){
            System.out.println("Sintaxe: java TcpSerializedTimeServerIncomplete listeningPort");
            return;
        }
        
        try(ServerSocket socket = new ServerSocket(Integer.parseInt(args[0]))){

            System.out.println("TCP Time Server iniciado no porto " + socket.getLocalPort() + " ...");

            while(true){

                try(Socket toClientSocket = socket.accept();
                    ObjectOutputStream oout = ...;
                    ObjectInputStream oin = ...){
                    
                    request = ...;

                    if(request == null){ //EOF
                        continue; //to next client request
                    }

                    System.out.println("Recebido \"" + request.trim() + "\" de " + 
                            toClientSocket.getInetAddress().getHostAddress() + ":" + 
                            toClientSocket.getPort());

                    if(!request.equalsIgnoreCase(TIME_REQUEST)){
                        System.out.println("Unexpected request");
                        continue;
                    }
                    
                    calendar = GregorianCalendar.getInstance();
                    
                    oout....;
                    oout.flush();

                }catch(Exception e){
                    System.out.println("Problema na comunicacao com o cliente " + 
                            toClientSocket.getInetAddress().getHostAddress() + ":" + 
                                toClientSocket.getPort()+"\n\t" + e);
                }
            }
            
        }catch(NumberFormatException e){
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        }catch(IOException e){
            System.out.println("Ocorreu um erro ao nivel do socket de escuta:\n\t"+e);
        }
    }
           
}

