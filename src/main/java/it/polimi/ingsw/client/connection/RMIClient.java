package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.clientFSM.ClientFSMState;
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

    /**
     * This function can be invoked to request the updated schema card or the complete schema card (in case of reconnection
     * or if it’s the beginning of the first round).The draft option makes the server send the four schema cards the user
     * has to choose from.
     * @return the list of four schema cards immutable objects
     */
    @Override
    public List<LightSchemaCard> getSchemaDraft() {
        List <LightSchemaCard>result = null;
        try {
            result = remoteObj.getSchemaDraft();
        } catch (RemoteException | IllegalActionException e) {
            client.disconnect();
        }
        return result;
    }

    /**
     * This function can be invoked to request the updated schema card or the complete schema card (in case of reconnection
     * or if it’s the beginning of the first round) of a scecific user.
     * @param playerId the id of the player's desired schema card
     * @return one schema card immutable object
     */
    @Override
    public LightSchemaCard getSchema(int playerId) {
        LightSchemaCard schema=null;
        try {
            schema = remoteObj.getSchema(playerId);
        } catch (RemoteException | IllegalActionException e) {
            client.disconnect();
        }
        return schema;
    }

    /**
     * This function can be invoked to request the private objective card parameters
     * @return one private objective card immutable object
     */
    @Override
    public LightPrivObj getPrivateObject() {
        LightPrivObj card = null;
        try {
            card = remoteObj.getPrivateObject();
        } catch (RemoteException e) {
            client.disconnect();
        }
        return card;
    }

    /**
     * This function can be invoked to request the three public objective cards parameters
     * @return a list of three public objective cards immutable objects
     */
    @Override
    public List<LightCard> getPublicObjectives() {
        List<LightCard> result = null;
        try {
            result = remoteObj.getPublicObjects();
        } catch (RemoteException e) {
            client.disconnect();
        }
        return result;
    }

    /**
     * This function can be invoked to request the three toolcards parameters
     * @return a list of three toolcards immutable objects
     */
    @Override
    public List<LightTool> getTools() {
        List<LightTool> result = null;
        try {
            result = remoteObj.getTools();
        } catch (RemoteException e) {
            client.disconnect();
        }
        return result;
    }

    /**
     * This function can be invoked to request the dice in the draftpool
     * @return a list of immutable dice contained in the draftpool
     */
    @Override
    public List<LightDie> getDraftPool() {
        List<LightDie> draftPool = null;
        try {
            draftPool = remoteObj.getDraftPool();
        } catch (RemoteException | IllegalActionException e) {
            client.disconnect();
        }
        return draftPool;
    }

    /**
     * This function can be invoked to request the dice in the roundtrack
     * @return a list of immutable dice contained in the roundtrack
     */
    @Override
    public List<List<LightDie>> getRoundtrack() {
        List<List<LightDie>> roundTrack=null;
        try {
            roundTrack = remoteObj.getRoundTrack();
        } catch (RemoteException | IllegalActionException e) {
            client.disconnect();
        }
        return roundTrack;

    }

    /**
     * The client invokes this function to request the list of players of the match
     * @return a list of immutable players that are playing the match
     */
    @Override
    public List<LightPlayer> getPlayers() {
        List<LightPlayer> players = null;
        try {
            players = remoteObj.getPlayers();
        } catch (RemoteException e) {
            client.disconnect();
        }
        return players;
    }

    /**
     * The client invokest this function to retireve the necessary information to
     * guarantee the correct reconnection during the game.
     * @return the match status
     */
    @Override
    public LightGameStatus getGameStatus() {
        LightGameStatus status=null;
        try {
            status = remoteObj.getGameStatus();
        } catch (RemoteException e) {
            client.disconnect();
        }
        return status;
    }

    /**
     * This function can be invoked to get the number of tokens remaining to the specified player.
     * @param playerId the id of the player (0 to 3)
     * @return the number of favor tokens of the specific player
     */
    @Override
    public int getFavorTokens(int playerId) {
        int favorTokens = 0;
        try{
            favorTokens = remoteObj.getFavorTokens(playerId);
        }catch(RemoteException e){
            client.disconnect();
        }
        return favorTokens;
    }

    /**
     * This function can be invoked to obtain an immutable and indexed list containing the information about the dice placed
     * in the actual selected element (schema,draftpool,roundtrack....)
     * @return an immutable and indexed list containing the dice
     */
    @Override
    public List<IndexedCellContent> getDiceList() {
        List<IndexedCellContent> diceList=null;
        try{
            diceList = remoteObj.getDiceList();
        }catch(RemoteException | IllegalActionException e){
            client.disconnect();
        }
        return diceList;
    }

    /**
     * This function can be invoked to select one die of a previolsly SELECT_DIE command and obtain
     * a list of to options to manipulate it
     * @param dieIndex the index of the die to select
     * @return and immutable and indexed list containing the dice
     */
    @Override
    public List<Actions> select(int dieIndex) {
        List<Actions> options=null;
        try{
            options = remoteObj.select(dieIndex);
        }catch(RemoteException | IllegalActionException e){
            client.disconnect();
        }
        return options;
    }

    /**
     * This function can be invoked by the client to request the list of possible placements of a die (that is
     * temporarily selected by the user) in his schema card
     * @return an immutable and indexed list of possible placements
     */
    @Override
    public List<Integer> getPlacementsList() {
        List<Integer> placements=null;
        try{
            placements = remoteObj.getPlacementsList();
        }catch(RemoteException | IllegalActionException e){
            client.disconnect();
        }
        return placements;
    }

    /**
     *  This function can be invoked to notify the server in order to make a possibly definitive choice. The server is
     *  still going to do his checks and will reply.
     * @param optionIndex the index of the object in the list previously sent by the server
     * @return true if the procedure is successful
     */
    @Override
    public boolean choose(int optionIndex) {
        try{
            return remoteObj.choose(optionIndex);
        }catch(RemoteException | IllegalActionException e){
            client.disconnect();
        }
        return false;
    }

    /**
     * This function can be invoked to notify the server the intenction to select a tool car. The server is
     * still going to do his checks and will reply.
     * @param toolIndex the index of the toolcard the user wants to use
     * @return true iff the toolcard has been activated
     */
    @Override
    public boolean enableTool(int toolIndex) {
        try{
            return remoteObj.enableTool(toolIndex);
        }catch(RemoteException | IllegalActionException e){
            client.disconnect();
        }
        return false;
    }

    /**
     * This function is invoked by the client to know if the toolcard's execution flow is still active
     * @return true iff the toolcard is active
     */
    @Override
    public boolean toolCanContinue() {
        try{
            return remoteObj.toolCanContinue();
        }catch(RemoteException | IllegalActionException e){
            client.disconnect();
        }
        return false;
    }

    /**
     * This function can be invoked to notify the server in case the client wants to end his turn before the timer goes off.
     */
    @Override
    public void endTurn() {
        try {
            remoteObj.endTurn();
        } catch (RemoteException | IllegalActionException e) {
            client.disconnect();
        }
    }

    /**
     * This message is sent to the server when the client that received a list of possible placement for a die chooses
     * not to place that die
     */
    @Override
    public void discard() {
        try {
            remoteObj.discard();
        } catch (RemoteException | IllegalActionException e) {
            client.disconnect();
        }
    }

    /**
     * This message is sent to the server when the client wants to stop using a toolcard before it ends
     */
    @Override
    public void back() {
        try {
            remoteObj.back();
        } catch (RemoteException | IllegalActionException e) {
            client.disconnect();
        }
    }

    /**
     * This method tells the server the end of the session
     */
    @Override
    public void quit() {
        try {
            remoteObj.quit();
        } catch (RemoteException e) {
            //do nothing already disconnecting
        }
    }

    /**
     * This function is invoked in case the client wants to start a new match when the previously is just ended
     */
    @Override
    public void newMatch() {
        try {
            remoteObj.newMatch();
        } catch (RemoteException e) {
            client.disconnect();
        }
    }

    /**
     * Tests if the client is still connected
     * @return true if the client is connected
     */
    @Override
    public void pong(){
        new Thread(() -> {
            boolean ping=true;
            while(ping && client.isLogged()) {
                try {
                    ping = remoteObj.pong();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("PONG RMI");
                } catch (RemoteException e) {
                    if(client.isLogged()) {
                        client.disconnect();
                        ping = false;
                        System.out.println("CONNECTION TIMEOUT!");
                    }
                }
            }
        }).start();
    }

    /*
     *  Disabled for RMI
     */
    @Override
    public boolean login(String username, char[] password) {
        return false;
    }

}

