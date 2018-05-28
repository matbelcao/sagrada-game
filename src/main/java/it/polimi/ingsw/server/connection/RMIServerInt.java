package it.polimi.ingsw.server.connection;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInt extends Remote {


    boolean ping() throws RemoteException;

    void notifyLobbyUpdate(int n) throws RemoteException;

    void notifyGameStart(int n, int id) throws RemoteException;
}
