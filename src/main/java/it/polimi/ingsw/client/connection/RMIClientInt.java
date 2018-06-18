package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIClientInt extends Remote {

    List<LightSchemaCard> getSchemaDraft() throws RemoteException, IllegalActionException;

    LightSchemaCard getSchema(int playerId) throws RemoteException, IllegalActionException;

    LightPrivObj getPrivateObject() throws RemoteException;

    List<LightCard> getPublicObjects() throws RemoteException;

    List<LightTool> getTools() throws RemoteException;

    List<LightDie> getDraftPool() throws RemoteException, IllegalActionException;

    List<List<LightDie>> getRoundTrack() throws RemoteException, IllegalActionException;

    List<LightPlayer> getPlayers() throws RemoteException;

    int getFavorTokens(int playerId) throws RemoteException;

    List<IndexedCellContent> getDiceList() throws RemoteException, IllegalActionException;

    List<Commands> select(int dieIndex) throws RemoteException, IllegalActionException;

    List<Integer> getPlacementsList() throws RemoteException, IllegalActionException;

    boolean choose(int optionIndex) throws RemoteException, IllegalActionException;

    boolean enableTool(int toolIndex) throws RemoteException, IllegalActionException;

    boolean toolCanContinue() throws RemoteException, IllegalActionException;

    void endTurn() throws RemoteException, IllegalActionException;

    void discard() throws RemoteException, IllegalActionException;

    void exit() throws RemoteException, IllegalActionException;

    void quit() throws RemoteException;

    boolean pong() throws RemoteException;
}