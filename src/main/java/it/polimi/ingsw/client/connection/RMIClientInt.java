package it.polimi.ingsw.client.connection;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInt extends Remote {
    void print(String message) throws RemoteException;

    boolean pong() throws RemoteException;

    public void updateLobby(int lobbySize) throws RemoteException;

    void updateGameStart(int n, int id) throws RemoteException;
}