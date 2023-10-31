package exercicio16;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteTimeInterface extends Remote {
    public Hora getHora() throws RemoteException;

    public void addMsg(String msg) throws RemoteException;
    public String getLastMsg() throws RemoteException;
    public List<String> getMsgs() throws RemoteException;
}
