import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TCPServer {
    public static final String TIME_REQUEST = "TIME";

    public static void main(String[] args) {

        int listeningPort;
        String receivedMsg;

        if (args.length != 1) {
            System.out.println("Sintaxe: java TcpSerializedTimeServer listeningPort");
            return;
        }

        listeningPort = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            System.out.println("TCP Time Server iniciado...");

            while (true) {

                //método bloqueante que fica aqui parado enquanto não receber um accept
                try(Socket socket = serverSocket.accept()) {


                    ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
                    receivedMsg = (String) oin.readObject();
                    System.out.println("Recebido de: " + receivedMsg + "Cliente conectado desde " + socket.getInetAddress() + ":" + socket.getPort());

                    if (!receivedMsg.equalsIgnoreCase(TIME_REQUEST)) continue;

                    ObjectOutputStream bout = new ObjectOutputStream(socket.getOutputStream());
                    Time time = new Time(Calendar.getInstance().get(GregorianCalendar.HOUR_OF_DAY), Calendar.getInstance().get(GregorianCalendar.MINUTE), Calendar.getInstance().get(GregorianCalendar.SECOND));
                    bout.writeObject(time);
                    bout.flush();

                    System.out.println("Enviado para cliente.");

                } catch (UnknownHostException e) {
                    System.out.println("Destino desconhecido:\n\t" + e);
                } catch (NumberFormatException e) {
                    System.out.println("O porto do servidor deve ser um inteiro positivo:\n\t" + e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Problema:\n\t" + e);
                } catch (SocketTimeoutException e) {
                    System.out.println("Não foi recebida qualquer resposta:\n\t" + e);
                } catch (IOException e) {
                    System.out.println("Ocorreu um erro de acesso ao socket:\n\t" + e);
                } catch (Exception e) {
                    System.out.println("Problema:\n\t" + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Time implements Serializable {
    private static final long serialVersionUID = 42L;

    private int horas, minutos, segundos;

    public Time(int horas, int minutos, int segundos) {
        this.horas = horas;
        this.minutos = minutos;
        this.segundos = segundos;
    }
}
