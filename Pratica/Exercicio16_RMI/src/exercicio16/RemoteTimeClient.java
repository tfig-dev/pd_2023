package exercicio16;

import java.rmi.*;

public class RemoteTimeClient {

    public static void main(String args[]) {
        System.out.println ("Looking for Remote Time service");

        try {
            String objectURL = "rmi://localhost/timeserver";

            if (args.length > 0) {
                objectURL = "rmi://" + args[0] + "/timeserver";
            }

            //String ipDMoreira_service = "10.65.136.231";
            Remote remoteService = Naming.lookup (objectURL);

            // CAST TO AN INTERFACE
            RemoteTimeInterface remoteTimeService = (RemoteTimeInterface) remoteService;

            System.out.println("Remote Time: " + remoteTimeService.getHora());

            remoteTimeService.addMsg("SPORTING");
            remoteTimeService.addMsg("SPORTING2");

            System.out.println("Last: " + remoteTimeService.getLastMsg());
            System.out.println("ALL: " + remoteTimeService.getMsgs());

        } catch (NotBoundException e) {
            System.out.println ("Remote Time service unavailable!");
        } catch (RemoteException e) {
            System.out.println ("RMI Error - " + e);
        } catch (Exception e) {
            System.out.println ("Error - " + e);
        }
    }
}