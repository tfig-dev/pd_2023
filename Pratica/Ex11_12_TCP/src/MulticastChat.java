/*Neste exemplo, a informacao e' trocada via um objecto
serializado que encapsula o "nickname" e a mensagem.*/

/*
   Multicast IPv4 em Mac OS
   Na lina de comando: -Djava.net.preferIPv4Stack=true
   No c�digo: System.setProperty("java.net.preferIPv4Stack", "true");
*/

import java.io.*;
import java.net.*;

public class MulticastChat extends Thread {
    public static final String LIST = "LIST";
    public static String EXIT = "EXIT";
    public static int MAX_SIZE = 1000;

    protected String username;
    protected MulticastSocket s;
    protected boolean running;

    public MulticastChat(String username, MulticastSocket s){
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
        Object obj;

        if(s == null || !running){
            return;
        }

        try{
            while(running){

                pkt = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                s.receive(pkt);

                //o facto de só usarmos o getData não garante que ele leia a mensagem toda
                //try (ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(pkt.getData()))) {
                try (ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()))) {

                    obj = oin.readObject();

                    System.out.println();
                    System.out.print("(" + pkt.getAddress().getHostAddress() + ":" + pkt.getPort() + ") ");

                    //Caso o objecto recebido seja uma instancia de Msg...
                    if(obj instanceof Msg){

                        msg = (Msg) obj;

                        if (msg.getMsg().toUpperCase().contains(LIST)) {

                            try (ByteArrayOutputStream buff = new ByteArrayOutputStream();
                                ObjectOutputStream bout = new ObjectOutputStream(buff)) {

                                bout.writeObject(username);

                                //Envia o username 'a origem sob a forma de um objecto serializado do tipo String
                                pkt.setData(buff.toByteArray(), 0, buff.size());
                                //ou o código de cima ou as 2 linhas debaixo
                                //pkt.setData(buff.toByteArray());
                                //pkt.setLength(buff.size());

                            } catch (IOException e) {
                                System.out.println();
                                System.out.println("Erro ao serializar a mensagem!");
                            }

                            s.send(pkt);
                            continue;
                        }

                        //Mostra a mensagem recebida bem como a identificacao do emissor
                        System.out.println("Recebido \"" + msg.getMsg() + "\" de " + msg.getNickname());

                        //Caso o objecto recebido seja uma instancia de String...
                    } else if (obj instanceof String) {

                        //Mostra a String
                        System.out.println("String: " + obj.toString());
                    }

                    System.out.println();
                    System.out.print("> ");

                }catch(ClassNotFoundException e){
                    System.out.println();
                    System.out.println("Mensagem recebida de tipo inesperado!");
                }catch(IOException e){
                    System.out.println();
                    System.out.println("Impossibilidade de aceder ao conteudo da mensagem recebida!");
                }
            }

        } catch(IOException e) {
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
        DatagramPacket dgram = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String msg;
        NetworkInterface nif;

        MulticastChat t = null;

        if(args.length != 4){
            System.out.println("Sintaxe: java MulticastChat <nickname> <grupo multicast> <porto> <interface de rede usada para multicast>");
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
            t = new MulticastChat(args[0], socket);
            //t.setDaemon(true);
            //thread deamon quer dizer que ela está a correr mas não nos interessa se ela termina ou não
            t.start();

            System.out.print("> ");

            while(true) {
                msg = in.readLine();

                if(msg.equalsIgnoreCase(EXIT)){
                    break;
                }

                //Envia para o grupo de multicast e porto escolhidos uma instancia de Msg
                //Receita para escrever num pacote um objeto serializado
                try (ByteArrayOutputStream buff = new ByteArrayOutputStream();
                    ObjectOutputStream bout = new ObjectOutputStream(buff)) {

                    bout.writeObject(new Msg(args[0], msg));
                    dgram = new DatagramPacket(buff.toByteArray(), buff.size(), group, port);

                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println();
                    System.out.println("Erro ao serializar a mensagem!");
                }

                socket.send(dgram);
            }

        } finally {

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


class Msg implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nickname;
    protected String msg;

    public Msg(String nickname, String msg){
        this.nickname = nickname;
        this.msg = msg;
    }

    public String getNickname() {
        return this.nickname;
    }
    public String getMsg(){
        return this.msg;
    }

}

