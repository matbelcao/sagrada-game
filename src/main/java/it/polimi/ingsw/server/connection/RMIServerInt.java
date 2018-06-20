package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.serializables.Event;
import it.polimi.ingsw.common.serializables.LightPlayer;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServerInt extends Remote {

    void notifyLobbyUpdate(int n) throws RemoteException;

    void notifyGameStart(int n, int id) throws RemoteException;

    void notifyGameEnd(List<RankingEntry> ranking) throws RemoteException;

    void notifyRoundEvent(Event event,int roundNumber) throws RemoteException;

    void notifyTurnEvent(Event event,int playerId,int turnNumber) throws RemoteException;

    void notifyStatusUpdate (Event event, int id) throws RemoteException;

    void notifyBoardChanged() throws RemoteException;

    boolean ping() throws RemoteException;
}
