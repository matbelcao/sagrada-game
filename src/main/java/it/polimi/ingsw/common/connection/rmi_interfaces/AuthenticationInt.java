package it.polimi.ingsw.common.connection.rmi_interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

    public interface AuthenticationInt  extends Remote {

        boolean authenticate(String userName, char [] password) throws RemoteException ;

        void updateConnected(String username) throws RemoteException;

        void setRemoteReference(RMIClientInt remoteRef, String username) throws RemoteException;
    }