package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.Client;

import java.rmi.RemoteException;

public class RMIServerObject  implements RMIServerInt {
    Client client;
    public RMIServerObject(Client client) {
        this.client = client;
    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public void notifyLobbyUpdate(int n) {
        client.getClientUI().updateLobby(n);
    }

    @Override
    public void notifyGameStart(int n, int id) {
        client.updateGameStart(n,id);
    }
}
