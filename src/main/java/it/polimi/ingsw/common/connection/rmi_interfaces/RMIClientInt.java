package it.polimi.ingsw.common.connection.rmi_interfaces;

import it.polimi.ingsw.common.serializables.GameEvent;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIClientInt extends Remote {

    /**
     * The server notified to all players in the lobby after a successful login of a player that isn't reconnecting to a
     * match he was previously playing. The message is sent again to all said players whenever there is a change in the
     * number of the users in the lobby.
     * @param n number of the players waiting in the lobby to begin a new match
     */
    void notifyLobbyUpdate(int n) throws RemoteException;

    /**
     * Notifies that the game to which the user is playing is ready to begin
     * @param n the number of players that are participating to the new match
     * @param id the assigned number of the user receiving this notification
     */
    void notifyGameStart(int n, int id) throws RemoteException;

    /**
     * The server notifies the end of a match and sends to each client a list of fields that represent the ranking of
     * the match's players.
     * @param ranking the List containing the ranking
     */
    void notifyGameEnd(List<RankingEntry> ranking) throws RemoteException;

    /**
     * This message is sent whenever a round is about to begin or has just ended.
     * @param gameEvent the gameEvent that has occurred (start/end)
     * @param roundNumber the number of the round (0 to 9)
     */
    void notifyRoundEvent(GameEvent gameEvent, int roundNumber) throws RemoteException;

    /**
     * Notifies the beginning/ending of a turn
     * @param gameEvent the gameEvent that has occurred (start/end)
     * @param playerId the player's identifier (0 to 3)
     * @param turnNumber the number of the turn within the single round (0 to 1)
     */
    void notifyTurnEvent(GameEvent gameEvent, int playerId, int turnNumber) throws RemoteException;

    /**
     * Notifies to all connected users that the status of a certain player has been changed
     * @param gameEvent the new status of the player (reconnect|disconnect|quit)
     * @param id the id of the interested player
     * @param userName the username
     */
    void notifyStatusUpdate (GameEvent gameEvent, int id, String userName) throws RemoteException;

    /**
     * Notifies that some parameter in the board has changed. Triggers the update request of the receiving client
     */
    void notifyBoardChanged() throws RemoteException;

    /**
     * Tests if the client is still connected
     */
    boolean ping() throws RemoteException;
}
