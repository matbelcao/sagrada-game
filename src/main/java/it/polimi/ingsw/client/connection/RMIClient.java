package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.immutables.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RMIClient implements ClientConn {
    RMIClientInt remoteObj;

    public RMIClient(RMIClientInt remoteObj) throws RemoteException {
        this.remoteObj = remoteObj;
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
        return null;

    }

    @Override
    public LightCard getPublicObj() {
        return null;
    }

    @Override
    public LightTool getTools() {
        return null;
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
        return null;
    }

    @Override
    public ArrayList<LightSchemaCard> draftSchema() {
        return null;
    }

    @Override
    public boolean pong() {
        return false;
    }
}

