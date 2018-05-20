package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.RMIClientInt;
import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.UserStatus;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIConn extends UnicastRemoteObject implements ServerConn,RMIConnInt {
        private RMIClientInt clientReference;
        private User user;

    public RMIConn(User user) throws RemoteException{
        this.user = user;
    }

    @Override
    public void setClientReference(RMIClientInt remoteRef) {
        this.clientReference = remoteRef;
    }

    //debugging method, to be deleted
    @Override
    public void printToTerminal(String message) throws RemoteException {
        MasterServer.getMasterServer().printMessage(message); //se è quello che intendevi
        clientReference.print("message from the server");
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

    @Override
    public boolean ping() {
        try{
            clientReference.pong();
        } catch (Exception e) {
            return false;
        }
            return true;
    }



}