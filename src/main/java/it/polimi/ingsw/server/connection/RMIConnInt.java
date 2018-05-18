package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.server.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIConnInt  extends Remote {

    void RMIConn(User user) throws RemoteException;
}
