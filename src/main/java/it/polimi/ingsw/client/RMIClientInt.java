package it.polimi.ingsw.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInt extends Remote {
    void print(String message) throws RemoteException;

    boolean pong() throws RemoteException;
}