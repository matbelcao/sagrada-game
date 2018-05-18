package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.server.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIConn extends UnicastRemoteObject implements ServerConn,RMIConnInt {
    private User user;

    protected RMIConn() throws RemoteException {
    }

    @Override
    public void RMIConn(User user) throws RemoteException {
        this.user = user;
    }

    @Override
    public void notifyLobbyUpdate(int n) {

    }

    @Override
    public void notifyGameStart(int n, int id) {

    }

    @Override
    public void notifyStatusUpdate(String event, int id) {

    }
}