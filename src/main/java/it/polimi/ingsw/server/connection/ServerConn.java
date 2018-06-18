package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.server.model.*;

import java.util.List;

/**
 * This class is an interfaces that declares the common methods between SOCKET/RMI server-side connections
 */
public interface ServerConn {

    void notifyLobbyUpdate(int n);

    void notifyGameStart(int n,int id);

    void notifyStatusUpdate (String event,int id);

    void notifyGameEnd(List<LightPlayer> players);

    void notifyRoundEvent(String event,int roundNumber);

    void notifyTurnEvent(String event,int playerId,int turnNumber);

    void notifyBoardChanged();

    boolean ping();



}
