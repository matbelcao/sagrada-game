package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;

import java.rmi.RemoteException;
import java.util.List;

public class RMIClient implements ClientConn {
    RMIClientInt remoteObj; //user
    Client client;

    public RMIClient(RMIClientInt remoteObj, Client client) {
        this.remoteObj = remoteObj;
        this.client = client;
    }

    @Override
    public boolean login(String username, char[] password) {
        return false;
    }

    /**
     * Tells the server the client is quitting the game
     */
    @Override
    public void quit() {
        try {
            remoteObj.quit();
        } catch (RemoteException e) {
            //do nothing already disconnecting
        }
    }

    @Override
    public LightCard getPrivateObject() {
        LightCard card = null;
        try {
            card = remoteObj.getPrivateObject();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return card;
    }

    @Override
    public List<LightCard> getPublicObjects() {
        List<LightCard> result = null;
        try {
            result = remoteObj.getPublicObjects();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<LightTool> getTools() {
        List<LightTool> result = null;
        try {
            result = remoteObj.getTools();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<LightDie> getDraftPool() {
        List<LightDie> draftPool = null;
        try {
            draftPool = remoteObj.getDraftPool();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IllegalActionException e) {
            e.printStackTrace();
        }
        return draftPool;
    }

    @Override
    public List<List<LightDie>> getRoundtrack() {
        return null;
    }

    @Override
    public List<LightPlayer> getPlayers() {
        List<LightPlayer> players = null;
        try {
            players = remoteObj.getPlayers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return players;
    }

    @Override
    public int getFavorTokens(int playerId) { //TODO check if it should throw exception
        int favorTokens = 0;
        try{
            favorTokens = remoteObj.getFavorTokens(playerId);
        }catch(RemoteException e){
            e.printStackTrace();
        }
        return favorTokens;
    }

    @Override
    public LightSchemaCard getSchema(int playerId) {
        LightSchemaCard returnedCard=null;
        try {
            returnedCard = remoteObj.getSchema(playerId);
        } catch (RemoteException e) {
            e.printStackTrace(); //TODO handle lost connection
            return null;
        } catch (IllegalActionException e) {
            e.printStackTrace();
        }
        return returnedCard;
    }

    @Override
    public void endTurn() {

    }

    @Override
    public List<LightSchemaCard> getSchemaDraft() {
        List <LightSchemaCard>result = null;
        try {
           result = remoteObj.getSchemaDraft();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IllegalActionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<IndexedCellContent> getSchemaDiceList() {
        return null;
    }

    @Override
    public List<IndexedCellContent> getRoundTrackDiceList() {
        return null;
    }

    @Override
    public List<IndexedCellContent> getDraftpoolDiceList() {
        return null;
    }

    @Override
    public List<Integer> selectDie(int index) {
        return null;
    }


    @Override
    public boolean choose(String type, int index) {
        return false;
    }

    @Override
    public void discard() {

    }

    @Override
    public boolean pong() {
        return false;
    }

    @Override
    public void sendDebugMessage(String message) {

    }
}

