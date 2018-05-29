package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.immutables.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public Map<Integer,CellContent> getDraftPool() {
        return null;
    }

    @Override
    public Map<Integer,CellContent> getRoundtrack() {
        return null;
    }

    @Override
    public List<LightPlayer> getPlayers() {
        return null;
    }

    @Override
    public int getFavorTokens(int playerId) {
        return 0;
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
    public ArrayList<LightSchemaCard> draftSchema() {
        return null;
    }

    @Override
    public boolean pong() {
        return false;
    }

    @Override
    public void sendDebugMessage(String message) {

    }
}

