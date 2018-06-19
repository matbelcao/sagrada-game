package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;


import java.rmi.RemoteException;
import java.util.List;

public class RMIClient implements ClientConn{
    private RMIClientInt remoteObj; //user
    private Client client;

    public RMIClient(RMIClientInt remoteObj, Client client) {
        this.remoteObj = remoteObj;
        this.client = client;
    }

    @Override
    public boolean login(String username, char[] password) {
        return false;
    }

    @Override
    public List<LightSchemaCard> getSchemaDraft() {
        List <LightSchemaCard>result = null;
        try {
            result = remoteObj.getSchemaDraft();
        } catch (RemoteException | IllegalActionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public LightSchemaCard getSchema(int playerId) {
        LightSchemaCard schema=null;
        try {
            schema = remoteObj.getSchema(playerId);
        } catch (RemoteException | IllegalActionException e) {
            e.printStackTrace();
        }
        return schema;
    }

    @Override
    public LightPrivObj getPrivateObject() {
        LightPrivObj card = null;
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
        } catch (RemoteException | IllegalActionException e) {
            e.printStackTrace();
        }
        return draftPool;
    }

    @Override
    public List<List<LightDie>> getRoundtrack() {
        List<List<LightDie>> roundTrack=null;
        try {
            roundTrack = remoteObj.getRoundTrack();
        } catch (RemoteException | IllegalActionException e) {
            e.printStackTrace();
        }
        return roundTrack;

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
    public int getFavorTokens(int playerId) {
        int favorTokens = 0;
        try{
            favorTokens = remoteObj.getFavorTokens(playerId);
        }catch(RemoteException e){
            e.printStackTrace();
        }
        return favorTokens;
    }

    @Override
    public List<IndexedCellContent> getDiceList() {
        List<IndexedCellContent> diceList=null;
        try{
            diceList = remoteObj.getDiceList();
        }catch(RemoteException | IllegalActionException e){
            e.printStackTrace();
        }
        return diceList;
    }

    @Override
    public List<Actions> select(int die_index) {
        List<Actions> options=null;
        try{
            options = remoteObj.select(die_index);
        }catch(RemoteException | IllegalActionException e){
            e.printStackTrace();
        }
        return options;
    }

    @Override
    public List<Integer> getPlacementsList() {
        List<Integer> placements=null;
        try{
            placements = remoteObj.getPlacementsList();
        }catch(RemoteException | IllegalActionException e){
            e.printStackTrace();
        }
        return placements;
    }

    @Override
    public boolean choose(int index) {
        try{
            return remoteObj.choose(index);
        }catch(RemoteException | IllegalActionException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean enableTool(int tool_index) {
        try{
            return remoteObj.enableTool(tool_index);
        }catch(RemoteException | IllegalActionException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean toolCanContinue() {
        try{
            return remoteObj.toolCanContinue();
        }catch(RemoteException | IllegalActionException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void endTurn() {
        try {
            remoteObj.endTurn();
        } catch (RemoteException | IllegalActionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void discard() {
        try {
            remoteObj.discard();
        } catch (RemoteException | IllegalActionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void back() {
        try {
            remoteObj.back();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IllegalActionException e) {
            e.printStackTrace();
        }
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
    public boolean pong() {
        return false;
    }

}

