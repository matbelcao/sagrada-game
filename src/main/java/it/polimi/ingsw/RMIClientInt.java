package it.polimi.ingsw;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInt extends Remote {
    void print(String message) throws RemoteException;
}
