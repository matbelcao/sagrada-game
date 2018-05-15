package it.polimi.ingsw.server.connection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIAuthenticator extends UnicastRemoteObject implements AuthenticationInt {
    protected RMIAuthenticator() throws RemoteException {}

    @Override
    public boolean authenticate(String username, String password) {
        //create the remote object
        return MasterServer.getMasterServer().login(username,password);
    }
}
