package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.RMIClientInt;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIConnInt  extends Remote {


    void setClientReference(RMIClientInt remoteRef) throws RemoteException;

    void printToTerminal(String message) throws RemoteException;
}
