package it.polimi.ingsw.common.connection.rmi_interfaces;

import it.polimi.ingsw.common.serializables.GameEvent;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIClientInt extends Remote {

    void notifyLobbyUpdate(int n) throws RemoteException;

    void notifyGameStart(int n, int id) throws RemoteException;

    void notifyGameEnd(List<RankingEntry> ranking) throws RemoteException;

    void notifyRoundEvent(GameEvent gameEvent, int roundNumber) throws RemoteException;

    void notifyTurnEvent(GameEvent gameEvent, int playerId, int turnNumber) throws RemoteException;

    void notifyStatusUpdate (GameEvent gameEvent, int id, String userName) throws RemoteException;

    void notifyBoardChanged() throws RemoteException;

    boolean ping() throws RemoteException;
}
