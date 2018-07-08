package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.connection.ClientInt;
import it.polimi.ingsw.common.connection.rmi_interfaces.RMIClientInt;
import it.polimi.ingsw.common.serializables.GameEvent;
import it.polimi.ingsw.common.serializables.RankingEntry;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static it.polimi.ingsw.common.serializables.GameEvent.*;

public class RMIClientObject extends UnicastRemoteObject implements RMIClientInt {
    private ClientInt client;
    public RMIClientObject(ClientInt client) throws RemoteException {
        this.client = client;
    }

    /**
     * The server notified to all players in the lobby after a successful login of a player that isn't reconnecting to a
     * match he was previously playing. The message is sent again to all said players whenever there is a change in the
     * number of the users in the lobby.
     * @param n number of the players waiting in the lobby to begin a new match
     */
    @Override
    public void notifyLobbyUpdate(int n) {
        client.getClientUI().updateLobby(n);
    }

    /**
     * Notifies that the game to which the user is playing is ready to begin
     * @param n the number of players that are participating to the new match
     * @param id the assigned number of the user receiving this notification
     */
    @Override
    public void notifyGameStart(int n, int id) {
        client.addUpdateTask(new Thread(()->
            client.updateGameStart(n,id)
        ));
    }

    /**
     * The server notifies the end of a match and sends to each client a list of fields that represent the ranking of
     * the match's players.
     * @param ranking the List containing the ranking
     */
    @Override
    public void notifyGameEnd(List<RankingEntry> ranking){
        client.addUpdateTask(new Thread(()->
            client.updateGameEnd(ranking)
        ));
    }

    /**
     * This message is sent whenever a round is about to begin or has just ended.
     * @param gameEvent the gameEvent that has occurred (start/end)
     * @param roundNumber the number of the round (0 to 9)
     */
    @Override
    public void notifyRoundEvent(GameEvent gameEvent, int roundNumber){
        if(gameEvent.equals(ROUND_START)){
            client.addUpdateTask(new Thread(()->
                client.updateGameRoundStart(roundNumber)
            ));
        }else if (gameEvent.equals(ROUND_END)){
            client.addUpdateTask(new Thread(()->
                client.updateGameRoundEnd(roundNumber)
            ));
        }
    }

    /**
     * Notifies the beginning/ending of a turn
     * @param gameEvent the gameEvent that has occurred (start/end)
     * @param playerId the player's identifier (0 to 3)
     * @param turnNumber the number of the turn within the single round (0 to 1)
     */
    @Override
    public void notifyTurnEvent(GameEvent gameEvent, int playerId, int turnNumber){
        if(gameEvent.equals(TURN_START)){
            client.addUpdateTask(new Thread(()->
                client.updateGameTurnStart(playerId,turnNumber==0)
            ));
        }else if (gameEvent.equals(TURN_END)){
            client.addUpdateTask(new Thread(()->
                client.updateGameTurnEnd(playerId)
            ));
        }
    }


    /**
     * Notifies to all connected users that the status of a certain player has been changed
     * @param gameEvent the new status of the player (reconnect|disconnect|quit)
     * @param id the id of the interested player
     * @param userName the username
     */
    @Override
    public void notifyStatusUpdate (GameEvent gameEvent, int id, String userName){
        client.addUpdateTask(new Thread(()->
            client.updatePlayerStatus(id, gameEvent,userName)
        ));
    }

    /**
     * Notifies that some parameter in the board has changed. Triggers the update request of the receiving client
     */
    @Override
    public void notifyBoardChanged(){
        client.addUpdateTask(new Thread(()->
            client.getBoardUpdates()
        ));
    }

    /**
     * Tests if the client is still connected
     */
    @Override
    public boolean ping() {
        client.getClass();
        return true;
    }
}
