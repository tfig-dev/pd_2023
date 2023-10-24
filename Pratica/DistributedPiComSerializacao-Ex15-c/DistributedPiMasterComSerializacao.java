package pt.isec.pd.ex15c;

import java.io.*;
import java.net.*;
import java.util.*;

public class DistributedPiMasterComSerializacao
{
    static final int TIMEOUT = 10000;
    static final int REGISTER_TIMEOUT = 1000;
    static final int UDP_PORT = 5001;
   
    private static int getWorkers(List<Socket> workers)
    {

        Socket toClientSocket;
        DatagramPacket dpkt;

        workers.clear();

        /* Cria um server socket e um datagram socket, ambos num porto automatico.
         * Cria igualmente um object output stream que permita serializar objetos para um
         * array de bytes.
        */
        try(ServerSocket ss = /*...*/;
            DatagramSocket ds = /*...*/;
            ByteArrayOutputStream bout = /*...*/;
            ObjectOutputStream oout = /*...*/){

            ss.setSoTimeout(REGISTER_TIMEOUT);
            
            /*
             * Transmite o porto automatico sob a forma de um objecto serializado do tipo Integer
             * por difusao (endereco 255.255.255.255) para o porto definido por UDP_PORT.
             */
                         
            /*...*/
            
            dpkt = new DatagramPacket(bout.toByteArray(), bout.size(), /*...*/, UDP_PORT);

            /*...*/

            System.out.println("> A aguardar pedidos de ligacao no porto TCP " + ss.getLocalPort());                            
                
            try{
                while(true){
                   
                    /*
                     * Aguarda pelo estabelecimento de ligacoes e acrescenta-as 'a lista workers.
                     */
                    
                    toClientSocket = /*...*/
                    /*...*/  

                    System.out.print("> Estabelecida ligacao com o worker " + workers.size());
                    System.out.print(" [" + toClientSocket.getInetAddress().getHostName() + ":");
                    System.out.println(toClientSocket.getPort()+"]");    
                }
            }catch(SocketTimeoutException e){}                                   
            
        }catch(IOException e){
            System.err.println(); System.err.println(e);
            try{                                                
                /*
                 * Fecha todos os sockets em workers e
                 * esvazia a lista.
                 */
                
                /*...*/
                
            }catch(IOException ee){}
        }
        
        return workers.size();
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        long nIntervals;
               
        List<Socket> workers  = new ArrayList<Socket>();
        ObjectOutputStream output;
        ObjectInputStream input;
        
        int i, nWorkers = 0;
        double workerResult;
        double pi = 0;
        
        Calendar t1, t2;
        
        System.out.println();
        			
        if(args.length != 1){
            System.out.println("Sintaxe: java ParallelPi <numero de intervalos>");
            return;
        }
        
        nIntervals = Long.parseLong(args[0]);
        
        t1 = GregorianCalendar.getInstance();                
        nWorkers = getWorkers(workers);
        
        System.out.println("Numero de workers: " + nWorkers);
        
        if(nWorkers <= 0){
            return;
        }
        
        try{                        
            
            for(i=0; i<nWorkers; i++){
                output = new ObjectOutputStream(workers.get(i).getOutputStream());
                output.writeObject(new RequestToWorker(i+1, nWorkers, nIntervals));
                output.flush();               
            }

            System.out.println();
			
            for(i=0; i<nWorkers; i++){
                input = new ObjectInputStream(workers.get(i).getInputStream());
                workerResult = ((Double)input.readObject()).doubleValue();
                System.out.println("> Worker " + (i+1) + ": " + workerResult);
                pi += workerResult;
            }
            
        }catch(IOException e){            
            System.err.println("Erro ao aceder ao socket\n\t" + e);
            return;
        }catch(ClassNotFoundException e){
            System.err.println("Recebido objecto de tipo inesperado\n\t" + e);
            return;
        }finally{
            /*
             * Fecha todos os sockets em workers e
             * esvazia a lista.
             */

            /*...*/
        }

        t2 = GregorianCalendar.getInstance();

        System.out.println();
        System.out.println(">> Valor aproximado do pi: " + pi + " (calculado em " + 
                (t2.getTimeInMillis() - t1.getTimeInMillis()) + " msec.)");
        
    }
}
