package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.connection.RMIClientInt;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInt extends Remote {


    void setClientReference(RMIClientInt remoteRef) throws RemoteException;

    void printToTerminal(String message) throws RemoteException;

    int getLobby() throws RemoteException;
}
