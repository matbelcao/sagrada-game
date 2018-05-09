package it.polimi.ingsw;
import java.rmi.Remote;
import java.rmi.RemoteException;

    public interface AuthenticationInt  extends Remote {

        public boolean authenticate(String userName, String password) throws RemoteException ;
    }