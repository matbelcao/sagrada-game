package it.polimi.ingsw.client.connection;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInt extends Remote {
    boolean pong() throws RemoteException;

    void updateLobby(int lobbySize) throws RemoteException;

    void updateGameStart(int n, int id) throws RemoteException;

    void quit() throws RemoteException;
}