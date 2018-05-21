package it.polimi.ingsw.client;

import it.polimi.ingsw.server.connection.RMIServerInt;

import java.rmi.RemoteException;

public class RMIClient implements ClientConn,RMIClientInt {
    private RMIServerInt RMIconn;

    public RMIClient(RMIServerInt RMIconn){
        this.RMIconn = RMIconn;
    }

    public RMIServerInt getRMIconn() {
        return RMIconn;
    }
//debugging methods
    @Override
    public void printToServer(String message) {
        try {
            RMIconn.printToTerminal(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean login() {
        return false;
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
