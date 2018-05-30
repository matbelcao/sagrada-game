package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.immutables.*;

import java.rmi.RemoteException;
import java.util.List;

public class RMIClient implements ClientConn {
    RMIClientInt remoteObj; //user
    Client client;

    public RMIClient(RMIClientInt remoteObj, Client client) throws RemoteException {
        this.remoteObj = remoteObj;
        this.client = client;
    }

    @Override
    public boolean login(String username, String password) {
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
    public LightCard getPrivateObj() {
        LightCard card = null;
        try {
            card = remoteObj.getPrivateObj();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return card;
    }

    @Override
    public List<LightCard> getPublicObjs() {
        List<LightCard> result = null;
        try {
            result = remoteObj.getPublicObjs();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<LightCard> getTools() {
        List<LightCard> result = null;
        try {
            result = remoteObj.getTools();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<CellContent> getDraftPool() {
        List<CellContent> draftPool = null;
        try {
            draftPool = remoteObj.getDraftPool();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return draftPool;
    }

    @Override
    public List<List<CellContent>> getRoundtrack() {
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
        LightSchemaCard returnedCard;
        try {
            returnedCard = remoteObj.getSchema(playerId);
        } catch (RemoteException e) {
            e.printStackTrace(); //TODO handle lost connection
            return null;
        }
        return returnedCard;
    }

    @Override
    public List<LightSchemaCard> draftSchema() {
        List <LightSchemaCard>result = null;
        try {
           result = remoteObj.draftSchema();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Integer> selectDie(int index) {
        return null;
    }

    @Override
    public boolean selectTool(LightTool lightTool, int index) {
        return false;
    }

    @Override
    public boolean pong() {
        return false;
    }

    @Override
    public void sendDebugMessage(String message) {

    }
}

