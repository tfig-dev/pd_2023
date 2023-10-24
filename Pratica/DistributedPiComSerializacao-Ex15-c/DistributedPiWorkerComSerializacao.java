package pt.isec.pd.ex15c;

import java.io.*;
import java.net.*;

public class DistributedPiWorkerComSerializacao implements Runnable {
    static final int TIMEOUT = 60000;
    static final int UDP_PORT = 5001;
    static final int BUFFER_SIZE = 500;
        
    protected Socket s;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;
    
    public DistributedPiWorkerComSerializacao(Socket s)
    {
        this.s= s;
        in = null;
        out = null;
    }
    public double getMyResult(int myId, int nWorkers, long nIntervals)
    {
        long i;
        double dX, xi, myResult;
        
        if(nIntervals < 1 || nWorkers < 1 || myId <1 || myId > nWorkers){
            return 0.0;
        }
        
        dX = 1.0/nIntervals;
        myResult = 0;
        
        for (i = myId-1 ; i < nIntervals; i += nWorkers) {             
            xi = dX*(i + 0.5);
            myResult += (4.0/(1.0 + xi*xi));               
        }
        
        myResult *= dX;
        
        return myResult;
    }
    @Override
    public void run()
    {
        int myId;
        int nWorkers;
        long nIntervals;
        double myResult;
        RequestToWorker req;

        try(ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream())){
            
            s.setSoTimeout(TIMEOUT);
            this.in = in;
            this.out = out;
                        
            try{
                
                req = (RequestToWorker)in.readObject();
                
                myId = req.getId();        
                nWorkers = req.getnWorkers();
                nIntervals = req.getnIntervals();
                
                System.out.println("<" + Thread.currentThread().getName() + 
                    "> New request received - myId: " + myId + " nWorkers: " + nWorkers + " nIntervals: " + nIntervals);
             }catch(ClassNotFoundException e){
                System.err.println("<" + Thread.currentThread().getName() + ">: " + e);
                return;
            }  
            
            myResult = getMyResult(myId, nWorkers, nIntervals);
            
            out.writeObject(myResult);
            out.flush();
            
            System.out.format("<%s> %.10f\n", Thread.currentThread().getName(), myResult);
            
        }catch(IOException e){
            System.out.println("<" + Thread.currentThread().getName() + 
                    "> Erro ao aceder ao socket:\n\t" + e);
        }finally{
            try{
                if(s != null) s.close();
            }catch(IOException e){}            
        }
        
    }

    public static void main(String[] args) {

        Socket toMaster = null;
        DatagramPacket dpkt;
        int tcpPortNumber;
        Thread t;
        int nCreatedThreads = 0;

        /*
         * Cria um socket UDP associado ao porto UDP_PORT
         */
        try(DatagramSocket ds = /*...*/){
            
            while(true){

                /*
                 * Aguarda pela recepcao de um datagrama.
                 */

                dpkt = /*...*/
                ds./*...*/
                
                try{

                    System.out.println("<DistributedPiWorker>: received packet from " + dpkt.getAddress().getHostAddress() + ":" + dpkt.getPort());

                    /*
                     * "Deserealiza" o objecto recebido assumindo que e' um Integer serializado.
                     */
                    try(ObjectInputStream oin = /*...*/) {
                        tcpPortNumber = /*...*/;
                    }

                    System.out.println("<DistributedPiWorker>: TCP port number is " + tcpPortNumber);

                    /*
                     * Estabelece uma ligacao TCP no porto indicado no endereco de onde vem 
                     * o datagrama UDP recebido.
                     */
                    toMaster = /*...*/

                    nCreatedThreads++;
                    t = new Thread(new DistributedPiWorkerComSerializacao(toMaster), "Thread "+nCreatedThreads);
                    t.start(); 
                    
                }catch(IOException | ClassNotFoundException e){
                    System.out.println("<DistributedPiWorker> " + e);
                }  
            }
                    
        }catch(IOException e) {
            System.out.println("<DistributedPiWorker> " + e);
        }
                
    }
}
