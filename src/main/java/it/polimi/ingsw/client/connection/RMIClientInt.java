package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.immutables.CellContent;
import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.common.immutables.LightSchemaCard;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIClientInt extends Remote {
    boolean pong() throws RemoteException;



    void quit() throws RemoteException;

    LightSchemaCard getSchema(int playerId) throws RemoteException;

    LightCard getPrivateObject() throws RemoteException;

    List<LightCard> getPublicObjects() throws RemoteException;

    List<LightCard> getTools() throws RemoteException;

    List<LightSchemaCard> getSchemaDraft() throws RemoteException;

    List<LightPlayer> getPlayers() throws RemoteException;

    int getFavorTokens(int playerId) throws RemoteException;

    List<CellContent> getDraftPool() throws RemoteException;
}