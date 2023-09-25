import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.Calendar;

public class TCPServer {
    public static final int MAX_SIZE = 256;
    public static final String TIME_REQUEST = "TIME";

    //sintax para iniciar:
    //java TCPServer.java 6001

    public static void main(String[] args) {

        int listeningPort;
        String timeMsg, request;
        Calendar calendar;

        if(args.length != 1){
            System.out.println("Sintaxe: java TCPTimeServer listeningPort");
            return;
        }

        listeningPort = Integer.parseInt(args[0]);

        try(ServerSocket socket = new ServerSocket(listeningPort)){

            System.out.println("TCP Time Server iniciado...");

            while(true){

                try(Socket cli = socket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(cli.getInputStream()));
                    PrintStream out = new PrintStream(cli.getOutputStream(), true);

                    request = in.readLine();
                    cli.setSoTimeout(1000);

                    if (request == null)
                        continue;

                    System.out.println("Recebido \"" + request.trim() + "\" de " +
                            cli.getInetAddress().getHostAddress() + ":" + cli.getPort());

                    if(!request.equalsIgnoreCase(TIME_REQUEST)){
                        System.out.println("Unexpected error");
                        continue;
                    }

                    calendar = Calendar.getInstance();
                    timeMsg = calendar.get(Calendar.HOUR_OF_DAY)+":"+
                            calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);

                    out.println(timeMsg);
                }
            }

        } catch(NumberFormatException e){
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        } catch(SocketException e){
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t"+e);
        } catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }
    }
}