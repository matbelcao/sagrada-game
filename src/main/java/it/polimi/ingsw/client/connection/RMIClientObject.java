package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.server.connection.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClientObject extends UnicastRemoteObject implements RMIClientInt {
    private User user;

    public RMIClientObject(User user) throws RemoteException {
        this.user = user;
    }

    @Override
    public boolean pong() throws RemoteException {
        return true;
    }

    @Override
    public void quit() throws RemoteException {

    }
}
