package it.polimi.ingsw;

import it.polimi.ingsw.server.connection.RMIConnInt;

import java.rmi.RemoteException;

public class RMIClient implements ClientConn,RMIClientInt {
    private RMIConnInt RMIconn;

    public RMIClient(RMIConnInt RMIconn){
        this.RMIconn = RMIconn;
    }

    public RMIConnInt getRMIconn() {
        return RMIconn;
    }
//debugging methods
    @Override
    public void printToServer(String message) {
        try {
            RMIconn.printToTerminal(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
//debugging methods
    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public boolean pong() throws RemoteException {
        return true;
    }
}
