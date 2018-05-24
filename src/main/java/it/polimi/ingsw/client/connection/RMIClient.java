package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.connection.RMIServerInt;

import java.rmi.RemoteException;

public class RMIClient implements ClientConn,RMIClientInt {
    private RMIServerInt RMIconn;
    private Client client;
    int size = 0;
    int id = 0;

    public RMIClient(RMIServerInt RMIconn, Client client){
        this.RMIconn = RMIconn;
        this.client = client;
    }

    public RMIServerInt getRMIconn() {
        return RMIconn;
    }
//debugging methods

    public void printToServer(String message) {
        try {
            RMIconn.printToTerminal(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateLobby(int lobbySize){
        client.getClientUI().updateLobby(lobbySize);
    }


    @Override
    public boolean login(String username, String password) {
        return false;
    }

    @Override
    public void quit() {

    }

    @Override
    public void getPrivateObj() {

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
    public boolean ping() {
        return false;
    }

    //debugging methods
    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public boolean pong() throws RemoteException {
        return true;
    }
}
