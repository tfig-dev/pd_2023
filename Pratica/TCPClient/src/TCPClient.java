import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class TCPClient {
    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; //segundos

    //sintax para ligar a server:
    //java TCPClient.java ip_server port_server

    public static void main(String[] args)
    {
        InetAddress serverAddr = null;
//        DatagramPacket packet = null;

        int serverPort;
        String response;

        if(args.length != 2){
            System.out.println("Sintaxe: java UdpTimeClient serverAddress serverUdpPort");
            return;
        }

        try {
            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);

            try(Socket socket = new Socket(serverAddr, serverPort);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                socket.setSoTimeout(TIMEOUT*1000);

                out.println(TIME_REQUEST);
                out.flush();

                response = in.readLine();

            }

            if (response == null) {
                System.out.println("O servidor não está a emitir respostas!");
            } else {
                System.out.println("Response: ");
            }

            //******************************************************************
            //Exemplo de como retirar os valores da mensagem de texto
            try {
                StringTokenizer tokens = new StringTokenizer(response," :");

                int hour = Integer.parseInt(tokens.nextToken().trim());
                int minute = Integer.parseInt(tokens.nextToken().trim());
                int second = Integer.parseInt(tokens.nextToken().trim());

                System.out.println("Horas: " + hour + " ; Minutos: " + minute + " ; Segundos: " + second);
            } catch(NumberFormatException e){}

            //******************************************************************

        }catch(UnknownHostException e){
            System.out.println("Destino desconhecido:\n\t"+e);
        }catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        }catch(SocketTimeoutException e){
            System.out.println("Nao foi recebida qualquer resposta:\n\t"+e);
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }
    }

}