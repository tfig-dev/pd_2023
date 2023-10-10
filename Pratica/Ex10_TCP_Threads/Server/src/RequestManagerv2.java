import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;

//public class RequestManager extends Thread { ou
public class RequestManagerv2 implements Runnable {

    public static final String TIME_REQUEST = "TIME";
    Socket clientSocket;

    public RequestManagerv2(Socket socket){
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