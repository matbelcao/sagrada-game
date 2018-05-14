package it.polimi.ingsw;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Authenticator extends UnicastRemoteObject implements AuthenticationInt{
    protected Authenticator() throws RemoteException {}

    @Override
    public boolean authenticate(String username, String password) {
        //create the remote object
        return MasterServer.getMasterServer().login(username,password);
    }
}