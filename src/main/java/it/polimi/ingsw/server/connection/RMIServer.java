package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.common.serializables.GameEvent;
import it.polimi.ingsw.common.serializables.RankingEntry;
import it.polimi.ingsw.server.model.User;

import java.rmi.RemoteException;
import java.util.List;

public class RMIServer implements ServerConn {
        private RMIServerInt remoteObj; //client
        private User user;
        private boolean connectionOk;


    RMIServer(RMIServerInt remoteObj, User user){
        this.remoteObj = remoteObj;
        this.user = user;
        connectionOk=true;
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
            connectionOk=false;
        }

    }

    /**
     * Notifies that the game to which the user is playing is ready to begin
     * @param n the number of players that are participating to the new match
     * @param id the assigned number of the user receiving this notification
     */
    @Override
    public void notifyGameStart(int n, int id) {

        try {
            remoteObj.notifyGameStart(n, id);
        } catch (RemoteException e) {
            user.disconnect();
            connectionOk = false;
        }

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
            connectionOk=false;
        }
    }

    /**
     * This message is sent whenever a round is about to begin or has just ended.
     * @param gameEvent the gameEvent that has occurred (start/end)
     * @param roundNumber the number of the round (0 to 9)
     */
    @Override
    public void notifyRoundEvent(GameEvent gameEvent, int roundNumber) {

        try {
            remoteObj.notifyRoundEvent(gameEvent, roundNumber);
        } catch (RemoteException e) {
            user.disconnect();
            connectionOk = false;
        }
    }

    /**
     * Notifies the beginning/ending of a turn
     * @param gameEvent the gameEvent that has occurred (start/end)
     * @param playerId the player's identifier (0 to 3)
     * @param turnNumber the number of the turn within the single round (0 to 1)
     */
    @Override
    public void notifyTurnEvent(GameEvent gameEvent, int playerId, int turnNumber) {

        try {
            remoteObj.notifyTurnEvent(gameEvent, playerId, turnNumber);
        } catch (RemoteException e) {
            user.disconnect();
            connectionOk = false;
        }


    }

    /**
     * Notifies to all connected users that the status of a certain player has been changed
     * @param gameEvent the new status of the player (reconnect|disconnect|quit)
     * @param id the id of the interested player
     */
    @Override
    public void notifyStatusUpdate(GameEvent gameEvent, int id, String userName) {

        try {
            remoteObj.notifyStatusUpdate(gameEvent, id, userName);
        } catch (RemoteException e) {
            user.disconnect();
            connectionOk = false;
        }

    }

    /**
     * Notifies that some parameter in the board has changed. Triggers the update request of the receiving client
     */
    @Override
    public void notifyBoardChanged() {

        try {
            remoteObj.notifyBoardChanged();
        } catch (RemoteException e) {
            user.disconnect();
            connectionOk = false;
        }


    }

    /**
     * Tests if the client is still connected
     */
    @Override
    public void ping(){
        new Thread(() -> {
            boolean ping=true;
            while(ping && connectionOk) {
                try {
                    ping = remoteObj.ping();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("PING RMI");
                } catch (RemoteException e) {
                    if(user.getStatus()!=UserStatus.DISCONNECTED){
                        user.disconnect();
                        System.out.println("CONNECTION TIMEOUT!");
                    }
                    ping=false;
                }
            }
        }).start();
    }
}