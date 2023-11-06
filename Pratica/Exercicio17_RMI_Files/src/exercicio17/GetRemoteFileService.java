package exercicio17;

import java.io.*;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class GetRemoteFileService extends UnicastRemoteObject implements Remote, GetRemoteFileInterface {
    public static final String SERVICE_NAME = "servidor-ficheiros-pd";
    public static final int MAX_CHUNK_SIZE = 10000; //bytes
    
    protected File localDirectory;

    //Iniciar - definir diretorio onde ir procurar ficheiros
    //java exercicio17.GetRemoteFileService /Users/tfigueiredo/Desktop

    public GetRemoteFileService(File localDirectory) throws RemoteException
    {
        this.localDirectory = localDirectory;        
    }
    
    public byte [] getFileChunk(String fileName, long offset) throws IOException {

        String requestedCanonicalFilePath = null;
        byte [] fileChunk = new byte[MAX_CHUNK_SIZE];
        int nbytes;        
        
        fileName = fileName.trim();
        
        try {
            /*
             * Verifica se o ficheiro solicitado existe e encontra-se por baixo da localDirectory 
             */
            requestedCanonicalFilePath = new File(localDirectory+File.separator+fileName).getCanonicalPath();

            if(!requestedCanonicalFilePath.startsWith(localDirectory.getCanonicalPath()+File.separator)){
                System.out.println("Nao e' permitido aceder ao ficheiro " + requestedCanonicalFilePath + "!");
                System.out.println("A directoria de base nao corresponde a " + localDirectory.getCanonicalPath()+"!");
                return null;
            }

            /*
             * Abre o ficheiro solicitado para leitura.
             */
            try (FileInputStream requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath)) {

                /*
                 * Obtem um bloco de bytes do ficheiro colocando-os no array fileChunk e omitindo os primeiros offset bytes.
                 */
                requestedFileInputStream.skip(offset);
                nbytes = requestedFileInputStream.read(fileChunk);

                if(nbytes == -1){ //EOF
                    return null;
                }

                /*
                 * Se fileChunk nao esta' totalmente preenchido (MAX_CHUNCK_SIZE), recorre-se
                 * a um array auxiliar com tamanho correspondente ao numero de bytes efectivamente lidos.
                 */
                if(nbytes < fileChunk.length){
                    /*
                     * Aloca aux
                     */
                    byte [] aux = new byte[nbytes];

                   /*
                    * Copia os bytes obtidos do ficheiro de fileChunck para aux
                    */
                    System.arraycopy(fileChunk, 0, aux, 0, nbytes);

                    return aux;
                }

                return fileChunk;
            }

        } catch(FileNotFoundException e) {   //Subclasse de IOException
            System.out.println("Ocorreu a excepcao {" + e + "} ao tentar abrir o ficheiro!");
            throw new FileNotFoundException(fileName);
        } catch(IOException e) {
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
            throw new IOException(fileName, e.getCause());
        }
    }

    @Override
    public long getFileSize(String fileName) throws RemoteException, IOException {
        return new File(localDirectory+File.separator+fileName).length();
    }

    /*
     * Lanca e regista um servico com interface remota do tipo GetRemoteFileInterface
     * sob o nome dado pelo atributo estatico SERVICE_NAME.
     */
    static public void main(String []args)
    {
        File localDirectory;
        
        /*
         * Se existirem varias interfaces de rede activas na maquina onde corre esta aplicacao/servidor RMI,
         * convem definir de forma explicita o endereco que deve ser incluido na referencia remota do servico
         * RMI criado. Para o efeito, o endereco deve ser atribuido 'a propriedade java.rmi.server.hostname.
         *
         * Pode ser no codigo atraves do metodo System.setProperty():
         *      - System.setProperty("java.rmi.server.hostname", "10.65.129.232"); //O endereco usado e' apenas um exemplo
         *      - System.setProperty("java.rmi.server.hostname", args[1]); //Neste caso, assume-se que o endereco e' passado como segundo argumento na linha de comando
         * 
         * Tambem pode ser como opcao passada 'a maquina virtual Java:
         *      - java -Djava.rmi.server.hostname=10.202.128.22 GetRemoteFileService c:\temp\ //O endereco usado e' apenas um exemplo
         *      - No Netbeans: Properties -> Run -> VM Options -> -Djava.rmi.server.hostname=10.202.128.22 //O endereco usado e' apenas um exemplo
         */
        
        /*
         * Trata os argumentos da linha de comando
         */
        if(args.length != 1){
            System.out.println("Sintaxe: java GetFileUdpServer localRootDirectory");
            return;
        }        

        localDirectory = new File(args[0].trim());

        if(!localDirectory.exists()){
           System.out.println("A directoria " + localDirectory + " nao existe!");
           return;
       }

       if(!localDirectory.isDirectory()){
           System.out.println("O caminho " + localDirectory + " nao se refere a uma directoria!");
           return;
       }

       if(!localDirectory.canRead()){
           System.out.println("Sem permissoes de leitura na directoria " + localDirectory + "!");
           return;
       }
       
        try{
            /*
             * Lanca o rmiregistry localmente no porto TCP por omissao (1099).
             */
            try {
                LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                System.out.println("RMI registry lançado...");
            } catch(RemoteException e) {
                System.out.println("Registry provavelmente já em execucao na maquina local!");
            }            

            /*
             * Cria o servico
             */            
            GetRemoteFileService fileService = new GetRemoteFileService(localDirectory);
            
            System.out.println("Serviço " + SERVICE_NAME + " criado e em execucao)");

            /*
             * Regista o servico no rmiregistry local para que os clientes possam localiza'-lo, ou seja,
             * obter a sua referencia remota (endereco IP, porto de escuta, etc.).
             */

            Naming.bind("rmi://localhost/" + SERVICE_NAME, fileService);
            //...  
                   
            System.out.println("Servico " + SERVICE_NAME + " registado no registry...");
            
            /*
             * Para terminar um servico RMI do tipo UnicastRemoteObject:
             *          UnicastRemoteObject.unexportObject(timeService, true);
             */
            
        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }catch(Exception e){
            System.out.println("Erro - " + e);
            System.exit(1);
        }                
    }    
}
