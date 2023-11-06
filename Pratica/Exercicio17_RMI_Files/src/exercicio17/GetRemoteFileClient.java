package exercicio17;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.*;

public class GetRemoteFileClient {

    public static void main(String args[]) {

        //iniciar o servico
        //java exercicio17.GetRemoteFileClient 10.65.136.231 servidor-ficheiros-pd fuc.pdf
        //inciar com o servico iniciado localmente
        //java exercicio17.GetRemoteFileClient localhost servidor-ficheiros-pd fuc.pdf


        String userHome = System.getProperty("user.home"); // Get the user's home directory
        File localDirectory =  new File(userHome, "Desktop/jipes/");

        String fileName, localFilePath = null;

        String currentPath = localDirectory.getAbsolutePath(), objectURL = "";

        byte [] b;
        long offset;

        System.out.println ("Looking for Remote Time service");

        if (args.length == 3) {
            objectURL = "rmi://" + args[0] + "/" + args[1];
        }

        fileName = args[2].trim();
        localDirectory = new File(currentPath);

        if (localDirectory.exists() && localDirectory.isDirectory()) {
            System.out.println("Localização da diretoria: " + localDirectory.getPath());
        } else {
            System.out.println("Diretoria não encontrada!");
            return;
        }

        if(!localDirectory.canWrite() || !localDirectory.canRead() || !localDirectory.canExecute() || !localDirectory.canRead()){
            System.out.println("Sem permissoes de escrita na directoria " + localDirectory);
            return;
        }

        try {
            localFilePath = new File(localDirectory.getPath()+File.separator+fileName).getCanonicalPath();
        } catch(IOException ex) {
            System.out.println(ex);
            return;
        }

        try(FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath)){

            System.out.println("Ficheiro " + localFilePath + " criado.");

            Remote remoteService = Naming.lookup (objectURL);
            GetRemoteFileInterface remoteTimeService = (GetRemoteFileInterface) remoteService;
            
            offset = 0;

            while((b = remoteTimeService.getFileChunk(fileName, offset)) != null){
                localFileOutputStream.write(b);
                offset += b.length;
            }

            System.out.println("Transferência do ficheiro " + fileName + " concluida com o tamanho: " + remoteTimeService.getFileSize(fileName) + " bytes.");

        } catch (NotBoundException e) {
            System.out.println ("Remote Time service unavailable!");
        } catch (RemoteException e) {
            System.out.println ("RMI Error - " + e);
        } catch (FileNotFoundException e) {
            System.out.println ("File not found - " + e);
            try {
                Files.deleteIfExists(Paths.get(localFilePath));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            System.out.println ("IO Error - " + e);
        } catch (Exception e) {
            System.out.println ("Error - " + e);
        }
    }
}