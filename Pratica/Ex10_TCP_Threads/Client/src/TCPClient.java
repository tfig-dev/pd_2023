import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; // seconds

    public static void main(String[] args) throws UnknownHostException {
        InetAddress serverAddr = null;
        int serverPort = -1;
        Time response;

        if (args.length != 2) {
            System.out.println("Sintaxe: java TcpSerializedTimeClient serverAddress serverTcpPort");
            return;
        }

        serverAddr = InetAddress.getByName(args[0]);
        serverPort = Integer.parseInt(args[1]);


        //usamos o try porque fecha objetos que sejam closable
        try (Socket socket = new Socket(serverAddr, serverPort)) {

            socket.setSoTimeout(TIMEOUT * 1000);

            ObjectOutputStream bout = new ObjectOutputStream(socket.getOutputStream());
            bout.writeObject(TIME_REQUEST);
            bout.flush();

            ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
            response = (Time) oin.readObject();

            System.out.println(response.toString());

        } catch (ClassNotFoundException e) {
            System.out.println("Classe não encontrada:\n\t" + e);
        } catch (Exception e) {
            System.out.println("Problema:\n\t" + e);
        }
    }
}

class Time implements Serializable {
    private static final long serialVersionUID = 42L;

    private int horas, minutos, segundos;
    private transient int milisegundos;

    public Time(int horas, int minutos, int segundos) {
        this.horas = horas;
        this.minutos = minutos;
        this.segundos = segundos;
    }

    public Time(int horas, int minutos, int segundos, int milisegundos) {
        this(horas, minutos, segundos);
        this.milisegundos = milisegundos;
    }

    @Override
    public String toString() {
        return "Hora indicada pelo servidor -> " + this.horas + "h:" + this.minutos + "m::" + this.segundos + "s::" + this.milisegundos + "ms";
    }
}