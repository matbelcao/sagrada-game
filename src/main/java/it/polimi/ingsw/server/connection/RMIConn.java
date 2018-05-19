package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.RMIClientInt;
import it.polimi.ingsw.server.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIConn extends UnicastRemoteObject implements ServerConn,RMIConnInt {
    private User user;
    private RMIClientInt clientReference;


    public RMIConn() throws RemoteException {
        super();
    }

    public  RMIConn(User user) throws RemoteException {
        this.user = user;
    }

    @Override
    public void setClientReference(RMIClientInt remoteRef) {
        this.clientReference = remoteRef;
    }
    //debugging method, to be deleted
    @Override
    public void printToTerminal(String message) throws RemoteException {
        System.out.print(message);
        clientReference.print("message from the server");
    }


    @Override
    public void notifyLobbyUpdate(int n) {

    }

    @Override
    public void notifyGameStart(int n, int id) {

    }

    @Override
    public void notifyStatusUpdate(String event, int id) {

    }
}