package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.connection.RMIClientInt;
import it.polimi.ingsw.server.model.SchemaCard;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject implements ServerConn,RMIServerInt {
        private RMIClientInt clientReference;
        private User user;

    public RMIServer(User user) throws RemoteException{
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

    @Override //remote
    public int getLobby() throws RemoteException {
        return MasterServer.getMasterServer().getLobby();
    }


    @Override
    public void notifyLobbyUpdate(int n) {
        try {
            clientReference.print("LOBBY "+n+"\n");
        } catch (RemoteException e) {
            user.disconnect();
        }
    }

    @Override
    public void notifyGameStart(int n, int id) {

    }

    @Override
    public void notifyStatusUpdate(String event, int id) {

    }

    @Override
    public void notifySchema(SchemaCard schemaCard){

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