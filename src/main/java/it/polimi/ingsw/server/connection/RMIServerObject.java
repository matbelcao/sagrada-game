package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.Client;

import java.rmi.RemoteException;

public class RMIServerObject  implements RMIServerInt {
    Client client;
    public RMIServerObject(Client client) throws RemoteException {
        this.client = client;
    }

    @Override
    public boolean ping() throws RemoteException {
        return true;
    }

    @Override
    public void notifyLobbyUpdate(int n) throws RemoteException {
        client.getClientUI().updateLobby(n);
    }

    @Override
    public void notifyGameStart(int n, int id) throws RemoteException {
        client.updateGameStart(n,id);
    }
}
