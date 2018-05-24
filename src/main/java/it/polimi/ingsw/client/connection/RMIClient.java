package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.connection.RMIServerInt;

import java.rmi.RemoteException;

public class RMIClient implements ClientConn,RMIClientInt {
    private RMIServerInt RMIconn;
    private Client client;

    public RMIClient(RMIServerInt RMIconn, Client client){
        this.RMIconn = RMIconn;
        this.client = client;
    }

    public RMIServerInt getRMIconn() {
        return RMIconn;
    }

    /**
     * Updates the view of the lobby's current size
     * @param lobbySize the current size of the lobby
     */
    @Override
    public void updateLobby(int lobbySize){
        client.getClientUI().updateLobby(lobbySize);
    }

    /**
     *Updates the view of the beginning of the match
     * @param n number of players of the match
     * @param id the id of the player running the application
     */
    @Override
    public void updateGameStart(int n, int id) { client.getClientUI().updateGameStart(n,id); }//

    @Override
    public boolean login(String username, String password) {
        return false;
    }

    /**
     * Tells the server the client is quitting the game
     */
    @Override
    public void quit() {
        try {
            RMIconn.quit();
            //do nothing, client is already disconnecting
        } catch (RemoteException e) { }
    }

    @Override
    public Integer getPrivateObj() {
        return 0;

    }

    @Override
    public void getPublicObj() {

    }

    @Override
    public void getTools() {

    }

    @Override
    public void getDraftPool() {

    }

    @Override
    public void getRoundtrack() {

    }

    @Override
    public void getPlayers() {

    }

    @Override
    public void getFavorTokens(int playerId) {

    }

    @Override
    public void getSchema(int playerId) {

    }

    @Override
    public void draftSchema() {

    }

    @Override
    public boolean ping() { return false; }

    @Override
    public boolean pong() { return true; }
}
