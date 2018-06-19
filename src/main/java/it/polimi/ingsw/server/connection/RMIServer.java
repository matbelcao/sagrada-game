package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.serializables.Event;
import it.polimi.ingsw.common.serializables.RankingEntry;
import it.polimi.ingsw.server.model.User;

import java.rmi.RemoteException;
import java.util.List;

public class RMIServer implements ServerConn {
        private RMIServerInt remoteObj; //client
        private User user;


    RMIServer(RMIServerInt remoteObj, User user){
        this.remoteObj = remoteObj;
        this.user = user;
    }

    /**
     * The server notified to all players in the lobby after a successful login of a player that isn't reconnecting to a
     * match he was previously playing. The message is sent again to all said players whenever there is a change in the
     * number of the users in the lobby.
     * @param n number of the players waiting in the lobby to begin a new match
     */
    @Override
    public void notifyLobbyUpdate(int n) {
        try {
            remoteObj.notifyLobbyUpdate(n);
        } catch (RemoteException e) {
            user.disconnect();
        }

    }

    /**
     * Notifies that the game to which the user is playing is ready to begin
     * @param n the number of players that are participating to the new match
     * @param id the assigned number of the user receiving this notification
     */
    @Override
    public void notifyGameStart(int n, int id) {
        new Thread(()-> {
            try {
                remoteObj.notifyGameStart(n, id);
            } catch (RemoteException e) {
                user.disconnect();
            }
        }).start();
    }

    /**
     * The server notifies the end of a match and sends to each client a list of fields that represent the ranking of
     * the match's players.
     * @param ranking the List containing the ranking
     */
    @Override
    public void notifyGameEnd(List<RankingEntry> ranking) {
        try {
            remoteObj.notifyGameEnd(ranking);
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    /**
     * This message is sent whenever a round is about to begin or has just ended.
     * @param event the event that has occurred (start/end)
     * @param roundNumber the number of the round (0 to 9)
     */
    @Override
    public void notifyRoundEvent(Event event, int roundNumber) {
        new Thread(()-> {
            try {
                remoteObj.notifyRoundEvent(event, roundNumber);
            } catch (RemoteException e) {
                user.disconnect();
            }
        }).start();
    }

    /**
     * Notifies the beginning/ending of a turn
     * @param event the event that has occurred (start/end)
     * @param playerId the player's identifier (0 to 3)
     * @param turnNumber the number of the turn within the single round (0 to 1)
     */
    @Override
    public void notifyTurnEvent(Event event, int playerId, int turnNumber) {
        new Thread(()-> {
            try {
                remoteObj.notifyTurnEvent(event, playerId, turnNumber);
            } catch (RemoteException e) {
                user.disconnect();
            }
        }
        ).start();
    }

    /**
     * Notifies to all connected users that the status of a certain player has been changed
     * @param event the new status of the player (reconnect|disconnect|quit)
     * @param id the id of the interested player
     */
    @Override
    public void notifyStatusUpdate(Event event, int id) {
        try {
            remoteObj.notifyStatusUpdate(event,id);
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    /**
     * Notifies that some parameter in the board has changed. Triggers the update request of the receiving client
     */
    @Override
    public void notifyBoardChanged() {
        new Thread(()-> {
            try {
                remoteObj.notifyBoardChanged();
            } catch (RemoteException e) {
                user.disconnect();
            }
        }).start();

    }

    /**
     * Closes the connection with the client
     */
    @Override
    public void close() {
        try {
            remoteObj.close() ;
        } catch (RemoteException e) {
            user.disconnect();
        }
    }


    /**
     * Tests if the client is still connected
     * @return true if the client is connected
     */
    @Override
    public boolean ping(){
        boolean result;
        try {
            result = remoteObj.ping();
        } catch (RemoteException e) {
            return false;
        }
        return result;
    }
}