package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.immutables.LightPlayer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServerInt extends Remote {

    void notifyLobbyUpdate(int n) throws RemoteException;

    void notifyGameStart(int n, int id) throws RemoteException;

    public void notifyGameEnd(List<LightPlayer> players) throws RemoteException;

    public void notifyRoundEvent(String event,int roundNumber) throws RemoteException;

    public void notifyTurnEvent(String event,int playerId,int turnNumber) throws RemoteException;

    public void notifyStatusUpdate (String event,int id) throws RemoteException;

    public void notifyBoardChanged() throws RemoteException;

    public void close() throws RemoteException;

    boolean ping() throws RemoteException;
}
