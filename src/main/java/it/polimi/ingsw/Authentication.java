package it.polimi.ingsw;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Authentication extends UnicastRemoteObject implements AuthenticationInt{
    protected Authentication() throws RemoteException {}

    @Override
    public boolean authenticate(String userName, String password) {
        return MasterServer.getMasterServer().loginRMI(userName,password);
    }
}
