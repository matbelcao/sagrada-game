package it.polimi.ingsw.common.connection.interfacesrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface offers the server's authentication methods to the client
 */
    public interface AuthenticationInt  extends Remote {

    /**
     * This method allows the user to authenticate to the server
     * @param userName the username
     * @param password the password
     * @return true if the authentication went fine
     * @throws RemoteException
     */
     boolean authenticate(String userName, char [] password) throws RemoteException ;

    /**
     * inserts the user in the lobby on the masterserver
     * @param username the username
     * @throws RemoteException
     */
     void updateConnected(String username) throws RemoteException;

    /**
     * sets the remote reference of the client to allow the server to make callbacks on it
     * @param remoteRef the client reference
     * @param username the client username
     * @throws RemoteException
     */
     void setRemoteReference(RMIClientInt remoteRef, String username) throws RemoteException;
    }