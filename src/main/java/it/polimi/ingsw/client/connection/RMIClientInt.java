package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIClientInt extends Remote {
    boolean pong() throws RemoteException;



    void quit() throws RemoteException;

    LightSchemaCard getSchema(int playerId) throws RemoteException, IllegalActionException;

    LightPrivObj getPrivateObject() throws RemoteException;

    List<LightCard> getPublicObjects() throws RemoteException;

    List<LightTool> getTools() throws RemoteException;

    List<LightSchemaCard> getSchemaDraft() throws RemoteException, IllegalActionException;

    List<LightPlayer> getPlayers() throws RemoteException;

    int getFavorTokens(int playerId) throws RemoteException;

    List<LightDie> getDraftPool() throws RemoteException, IllegalActionException;
}