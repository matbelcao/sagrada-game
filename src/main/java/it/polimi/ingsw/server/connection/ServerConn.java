package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.serializables.Event;
import it.polimi.ingsw.common.serializables.LightPlayer;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.util.List;

/**
 * This class is an interfaces that declares the common methods between SOCKET/RMI server-side connections
 */
public interface ServerConn {

    void notifyLobbyUpdate(int n);

    void notifyGameStart(int n,int id);

    void notifyStatusUpdate (Event event,int id);

    void notifyGameEnd(List<RankingEntry> ranking);

    void notifyRoundEvent(Event event, int roundNumber);

    void notifyTurnEvent(Event event,int playerId,int turnNumber);

    void notifyBoardChanged();

    void close();

    boolean ping();

}
