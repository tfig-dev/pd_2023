package exercicio17;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GetRemoteFileInterface extends Remote {

    byte[] getFileChunk(String fileName, long offset) throws RemoteException, IOException;

    long getFileSize(String fileName) throws RemoteException, IOException;
}
