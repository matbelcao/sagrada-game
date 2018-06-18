package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.User;

import java.rmi.RemoteException;
import java.util.List;

public class RMIServer implements ServerConn {
        private RMIServerInt remoteObj; //client
        private User user;


    public RMIServer(RMIServerInt remoteObj,User user){
        this.remoteObj = remoteObj;
        this.user = user;
    }

    /**
     * Notifies the client waiting in a lobby that the lobby has updated
     * @param n lobby's current size
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
     * Notifies the client that the game has started
     * @param n the number of players playing the game
     * @param id the client's identification number in the game
     */
    @Override
    public void notifyGameStart(int n, int id) {
        try {
            remoteObj.notifyGameStart(n, id);
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    @Override
    public void notifyGameEnd(List<LightPlayer> players) {
        try {
            remoteObj.notifyGameEnd(players);
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    @Override
    public void notifyRoundEvent(String event, int roundNumber) {
        try {
            remoteObj.notifyRoundEvent(event,roundNumber);
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    @Override
    public void notifyTurnEvent(String event, int playerId, int turnNumber) {
        try {
            remoteObj.notifyTurnEvent(event,playerId,turnNumber);
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    @Override
    public void notifyStatusUpdate(String event, int id) {
        try {
            remoteObj.notifyStatusUpdate(event,id);
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    @Override
    public void notifyBoardChanged() {
        try {
            remoteObj.notifyBoardChanged();
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    @Override
    public void close() {
        try {
            remoteObj.close() ;
        } catch (RemoteException e) {
            user.disconnect();
        }
    }


    /**
     * Pings the client invoking a remote method
     * @ truee iff the remote call doesn't throw an exception, therefore the connession between client and server is still up
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