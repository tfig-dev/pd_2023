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

    public static void main(String[] args) {

        int listeningPort, threadNumber = 0;
        String receivedMsg;

        if (args.length != 1) {
            System.out.println("Sintaxe: java TcpSerializedTimeServer listeningPort");
            return;
        }

        listeningPort = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            System.out.println("TCP Time Server iniciado...");

            while (true) {

                Socket clientSocket = serverSocket.accept();
                RequestManager requestManager = new RequestManager(clientSocket);
                Thread thread = new Thread(requestManager, ("Thread_" + threadNumber++));
                thread.start();
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

//public class RequestManager extends Thread { ou
class RequestManager implements Runnable {

    public static final String TIME_REQUEST = "TIME";
    Socket clientSocket;

    public RequestManager(Socket socket){
        this.clientSocket = socket;
    }

    @Override
    public void run() {

        String receivedMsg;

        try (ObjectInputStream oin = new ObjectInputStream(clientSocket.getInputStream())) {

            receivedMsg = (String) oin.readObject();
            System.out.println(clientSocket.getInetAddress() + ":" + clientSocket.getPort() + "$ ~ command: " + receivedMsg);

            if (!receivedMsg.equalsIgnoreCase(TIME_REQUEST)) return;

            try {
                if (Thread.currentThread().getName().equalsIgnoreCase("Thread_0"))
                    Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            ObjectOutputStream bout = new ObjectOutputStream(clientSocket.getOutputStream());
            Time time = new Time(Calendar.getInstance().get(GregorianCalendar.HOUR_OF_DAY), Calendar.getInstance().get(GregorianCalendar.MINUTE), Calendar.getInstance().get(GregorianCalendar.SECOND));
            bout.writeObject(time);
            bout.flush();

            System.out.println(Thread.currentThread().getName() + " -> send info to client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Problema:\n\t" + e);
        }
    }
}