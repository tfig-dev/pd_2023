/*Neste exemplo, a informacao e' trocada via um objecto
serializado que encapsula o "nickname" e a mensagem.*/

/*
   Multicast IPv4 em Mac OS
   Na lina de comando: -Djava.net.preferIPv4Stack=true
   No c�digo: System.setProperty("java.net.preferIPv4Stack", "true");
*/
package pt.isec.pd.aula5.multicast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

class Msg {
	protected String nickname;
	protected String msg;
	
	public Msg(String nickname, String msg){
		/*...*/
	}
	
	public String getNickname(){ /*...*/ }
	public String getMsg(){ /*...*/ }	
        
}

public class MulticastChat_v2 extends Thread {
    public static final String LIST = "LIST";
    public static String EXIT = "EXIT";
    public static int MAX_SIZE = 1000;
    
    protected String username;
    protected MulticastSocket s;
    protected boolean running;

    public MulticastChat_v2(String username, MulticastSocket s){
        this.username = username;
        this.s = s;
        running = true;
    }
    
    public void terminate(){
        running = false;
    }
            
    @Override
    public void run() {
       
        DatagramPacket pkt;
        Msg msg;
        /*...*/   
        
        if(s == null || !running){
            return;
        }
        
        try{
            
            while(running){
                
                pkt = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                s.receive(pkt);
                
                try{                    
                    
                    // "Deserializa" o objecto transportado no datagrama acabado de ser recebido
                               
                    /*...*/ 
                    
                    System.out.println();
                    System.out.print("(" + pkt.getAddress().getHostAddress() + ":" + pkt.getPort() + ") ");
                    
                    //Caso o objecto recebido seja uma instancia de Msg...
                    if(/*...*/){
                        
                        msg = /*...*/
                        
                        if(msg.getMsg().toUpperCase().contains(LIST)){
                            
                            //Envia o username 'a origem sob a forma de um objecto serializado do tipo String
                            /*...*/
                            
                            s.send(pkt);
                            continue;
                        }
                        
                        //Mostra a mensagem recebida bem como a identificacao do emissor
                        System.out.println("Recebido \"" + /*...*/ + "\" de " + /*...*/);
                         
                    //Caso o objecto recebido seja uma instancia de String...
                    } else if(/*...*/){
                        
                        //Mostra a String
                        System.out.println(/*...*/);
                    }
                    
                    System.out.println(); System.out.print("> ");
                                          
                }catch(ClassNotFoundException e){
                    System.out.println();
                    System.out.println("Mensagem recebida de tipo inesperado!");
                }catch(IOException e){
                    System.out.println();
                    System.out.println("Impossibilidade de aceder ao conteudo da mensagem recebida!");
                }
                
            }
            
        }catch(IOException e){
            if(running){
                System.out.println(e);
            }
            
            if(!s.isClosed()){                
                s.close();
            }
        }   
        
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException {
     
        InetAddress group;
        int port;
        MulticastSocket socket = null;
        DatagramPacket dgram;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String msg;
        NetworkInterface nif;
        
        MulticastChat_v2 t = null;
        
        if(args.length != 4){
            System.out.println("Sintaxe: java MulticastChat <nickname> <groupo multicast> <porto> <interface de rede usada para multicast>");
            return;
        }
        
        try{
            group = InetAddress.getByName(args[1]);
            port = Integer.parseInt(args[2]);

            try{                
                nif = NetworkInterface.getByInetAddress(InetAddress.getByName(args[3])); //e.g., 127.0.0.1, 192.168.10.1, ... 
            }catch (SocketException | NullPointerException | UnknownHostException | SecurityException ex){               
                nif = NetworkInterface.getByName(args[3]); //e.g., lo0, eth0, wlan0, en0, ...
            }
                        
            socket = new MulticastSocket(port);
            socket.joinGroup(new InetSocketAddress(group, port), nif);

            //Lanca a thread adicional dedicada a aguardar por datagramas no socket e a processá-los
            t = new MulticastChat_v2(args[0], socket);
            //t.setDaemon(true);
            t.start();
            
            System.out.print("> ");
            
            while(true){              
                
                msg = in.readLine();
                
                if(msg.equalsIgnoreCase(EXIT)){
                    break;
                }
                
                //Envia para o grupo de multicast e porto escolhidos uma instancia de Msg
                /*...*/
                socket.send(dgram);   
                
            }
            
        }finally{
            
            if(t != null){
                t.terminate();
            }
            
            if(socket != null){
                socket.close();
            }

            //t.join(); //Para esperar que a thread termine caso esteja em modo daemon
            
        }

    }
}
