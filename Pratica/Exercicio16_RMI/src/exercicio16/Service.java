package exercicio16;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Service extends java.rmi.server.UnicastRemoteObject
        implements RemoteTimeInterface {

    List<String> msgs = new ArrayList<>();

    protected Service() throws RemoteException {}

    @Override
    public Hora getHora() throws java.rmi.RemoteException {
        Calendar calendar = GregorianCalendar.getInstance();
        return new Hora(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    @Override
    public void addMsg(String msg) throws RemoteException {
        //adiciona a mensagem à lista de mensagens
        msgs.add(msg);
    }

    @Override
    public String getLastMsg() throws RemoteException {
        //retorna a ultima mensagem da lista
        return msgs.get(msgs.size() - 1);
    }

    @Override
    public List<String> getMsgs() throws RemoteException {
        //retorna a lista de mensagens
        return msgs;
    }

    public static void main(String[] args) {
        try {

            //lançar o RMI Registry
            try {
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                System.out.println("RMI registry launched.");
            } catch (RemoteException e) {
                System.out.println("RMI registry already running.");
            }

            //Criar instancia e lançar o serviço
            //isto pode ser usado num RemoteTimeServer, por exemplo
            Service timeService = new Service();

            System.out.println("Serviço RemoteTime criado e em execução(" + timeService.getRef().remoteToString() + ")");

            //regista o serviço para que os clientes possam encontrá-lo, ou seja, obter a

            //bind() -> lanca uma execeçao se o nome já estiver associado a um objeto
            //rebind() -> substitui o objeto (faz override) associado ao nome se já existir

            Naming.bind("rmi://localhost/timeserver", timeService);

            System.out.println("Serviço RemoteTime registado no registry local com o nome \"timeserver\"");


        } catch (RemoteException e) {
            System.out.println("Remote Error - " + e);
            System.exit(1);
        } catch (MalformedURLException | AlreadyBoundException e) {
            System.out.println("Error - " + e);
            System.exit(1);
        }
    }
}