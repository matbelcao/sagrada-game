package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightSchemaCard;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInt extends Remote {
    boolean pong() throws RemoteException;



    void quit() throws RemoteException;

    LightSchemaCard getSchema(int playerId) throws RemoteException;

    LightCard getPrivateObj() throws RemoteException;

    LightCard getPublicObj() throws RemoteException;
}