package it.polimi.ingsw.server.connection;
import java.rmi.Remote;
import java.rmi.RemoteException;

    public interface AuthenticationInt  extends Remote {

        boolean authenticate(String userName, char [] password) throws RemoteException ;

        void updateConnected(String username) throws RemoteException;

        void setRemoteReference(RMIServerInt remoteRef, String username) throws RemoteException;
    }