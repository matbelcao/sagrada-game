package it.polimi.ingsw.server.connection;
import java.rmi.Remote;
import java.rmi.RemoteException;

    public interface AuthenticationInt  extends Remote {

        public boolean authenticate(String userName, String password) throws RemoteException ;

        void updateConnected(String username);
    }