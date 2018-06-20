package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.serializables.Event;
import it.polimi.ingsw.common.serializables.LightPlayer;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.util.List;

/**
 * This class is an interfaces that declares the common methods between SOCKET/RMI server-side connections
 */
public interface ServerConn {

    /**
     * The server notified to all players in the lobby after a successful login of a player that isn't reconnecting to a
     * match he was previously playing. The message is sent again to all said players whenever there is a change in the
     * number of the users in the lobby.
     * @param n number of the players waiting in the lobby to begin a new match
     */
    void notifyLobbyUpdate(int n);

    /**
     * Notifies that the game to which the user is playing is ready to begin
     * @param n the number of players that are participating to the new match
     * @param id the assigned number of the user receiving this notification
     */
    void notifyGameStart(int n,int id);

    /**
     * The server notifies the end of a match and sends to each client a list of fields that represent the ranking of
     * the match's players.
     * @param ranking the List containing the ranking
     */
    void notifyGameEnd(List<RankingEntry> ranking);

    /**
     * This message is sent whenever a round is about to begin or has just ended.
     * @param event the event that has occurred (start/end)
     * @param roundNumber the number of the round (0 to 9)
     */
    void notifyRoundEvent(Event event, int roundNumber);

    /**
     * Notifies the beginning/ending of a turn
     * @param event the event that has occurred (start/end)
     * @param playerId the player's identifier (0 to 3)
     * @param turnNumber the number of the turn within the single round (0 to 1)
     */
    void notifyTurnEvent(Event event,int playerId,int turnNumber);

    /**
     * Notifies to all connected users that the status of a certain player has been changed
     * @param event the new status of the player (reconnect|disconnect|quit)
     * @param id the id of the interested player
     */
    void notifyStatusUpdate (Event event,int id);

    /**
     * Notifies that some parameter in the board has changed. Triggers the update request of the receiving client
     */
    void notifyBoardChanged();

    /**
     * Tests if the client is still connected
     */
    void ping();

}
