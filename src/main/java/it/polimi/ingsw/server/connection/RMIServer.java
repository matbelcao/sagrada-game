package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.rmi_interfaces.RMIClientInt;
import it.polimi.ingsw.common.serializables.GameEvent;
import it.polimi.ingsw.common.serializables.RankingEntry;
import it.polimi.ingsw.server.controller.User;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIServer implements ServerConn {
    private RMIClientInt remoteObj; //client
    private User user;
    private boolean connectionOk;
    private final Object lockPing =new Object();
    private Timer pingTimer;

    private static final int PING_TIME=3000;
    private static final String CONNECTION_TIMEOUT = "CONNECTION TIMEOUT!";


    RMIServer(RMIClientInt remoteObj, User user){
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
            disconnect();
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
            disconnect();
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
            disconnect();
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
            disconnect();
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
            disconnect();
        }


    }

    /**
     * Notifies to all connected users that the status of a certain player has been changed
     * @param gameEvent the new status of the player (reconnect|disconnect|quit)
     * @param id the id of the interested player
     * @param userName the username
     */
    @Override
    public void notifyStatusUpdate(GameEvent gameEvent, int id, String userName) {

        try {
            remoteObj.notifyStatusUpdate(gameEvent, id, userName);
        } catch (RemoteException e) {
            disconnect();
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
            disconnect();
        }


    }

    /**
     * If triggered, it means that the connection has broken
     */
    private class ConnectionTimeout extends TimerTask {
        @Override
        public void run(){
            disconnect();
        }
    }

    /**
     * Closes the connection if broken
     */
    private void disconnect(){
        synchronized (lockPing) {
            if (connectionOk) {
                connectionOk = false;
                System.out.println(CONNECTION_TIMEOUT);
                user.disconnect();
            }
            lockPing.notifyAll();
        }
    }

    /**
     * This method provides the ping functionality for checking if the connection is still active
     */
    @Override
    public void ping(){
        new Thread(() -> {
            while(connectionOk) {
                try {
                    pingTimer = new Timer();
                    pingTimer.schedule(new ConnectionTimeout(), PING_TIME);
                    remoteObj.ping();
                    pingTimer.cancel();
                    try {
                        Thread.sleep(PING_TIME);
                    } catch (InterruptedException e) {
                        Logger.getGlobal().log(Level.INFO,e.getMessage());
                    }
                } catch (RemoteException e) {
                    disconnect();
                }
            }
        }).start();
    }
}