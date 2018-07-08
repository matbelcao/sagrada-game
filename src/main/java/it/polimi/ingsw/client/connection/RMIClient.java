package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.controller.Client;
import it.polimi.ingsw.client.controller.ClientFSMState;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.common.exceptions.IllegalActionException;
import it.polimi.ingsw.common.connection.rmi_interfaces.RMIServerInt;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the implementation of the RMI client-side connection methods
 */
public class RMIClient implements ClientConn{
    private RMIServerInt remoteObj; //user
    private Client client;
    private boolean connectionOk;
    private final Object lockPing =new Object();
    private Timer pingTimer;

    private static final int PONG_TIME=2000;
    private static final String CONNECTION_TIMEOUT = "CONNECTION TIMEOUT!";

    /**
     * instantiates the object
     * @param remoteObj the objet on the server
     * @param client the client object
     */
    public RMIClient(RMIServerInt remoteObj, Client client) {
        this.remoteObj = remoteObj;
        this.client = client;
        connectionOk=true;
    }

    /**
     * This function can be invoked to request the updated schema card or the complete schema card (in case of reconnection
     * or if it’s the beginning of the first round).The draft option makes the server send the four schema cards the user
     * has to choose from.
     * @return the list of four schema cards immutable objects
     */
    @Override
    public List<LightSchemaCard> getSchemaDraft() {
        List <LightSchemaCard>result = new ArrayList<>();
        try {
            result = remoteObj.getSchemaDraft();
        } catch (RemoteException e) {
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
        } catch (RemoteException e) {
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
            closeConn();
        }
        return card;
    }

    /**
     * This function can be invoked to request the three public objective cards parameters
     * @return a list of three public objective cards immutable objects
     */
    @Override
    public List<LightCard> getPublicObjectives() {
        List<LightCard> result = new ArrayList<>();
        try {
            result = remoteObj.getPublicObjectives();
        } catch (RemoteException e) {
            closeConn();
        }
        return result;
    }

    /**
     * This function can be invoked to request the three toolcards parameters
     * @return a list of three toolcards immutable objects
     */
    @Override
    public List<LightTool> getTools() {
        List<LightTool> result = new ArrayList<>();
        try {
            result = remoteObj.getTools();
        } catch (RemoteException e) {
            closeConn();
        }
        return result;
    }

    /**
     * This function can be invoked to request the dice in the draftpool
     * @return a list of immutable dice contained in the draftpool
     */
    @Override
    public List<LightDie> getDraftPool() {
        List<LightDie> draftPool =new ArrayList<>();
        try {
            draftPool = remoteObj.getDraftPool();
        } catch (RemoteException e) {
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
        }
        return draftPool;
    }

    /**
     * This function can be invoked to request the dice in the roundtrack
     * @return a list of immutable dice contained in the roundtrack
     */
    @Override
    public List<List<LightDie>> getRoundtrack() {
        List<List<LightDie>> roundTrack=new ArrayList<>();
        try {
            roundTrack = remoteObj.getRoundTrack();
        } catch (RemoteException e) {
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
        }
        return roundTrack;

    }

    /**
     * The client invokes this function to request the list of players of the match
     * @return a list of immutable players that are playing the match
     */
    @Override
    public List<LightPlayer> getPlayers() {
        List<LightPlayer> players = new ArrayList<>();
        try {
            players = remoteObj.getPlayers();
        } catch (RemoteException e) {
            closeConn();
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
            closeConn();
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
            closeConn();
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
        List<IndexedCellContent> diceList=new ArrayList<>();
        try{
            diceList = remoteObj.getDiceList();
        }catch(RemoteException  e){
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
        List<Actions> options=new ArrayList<>();
        try{
            options = remoteObj.select(dieIndex);
        }catch(RemoteException  e){
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
        List<Integer> placements=new ArrayList<>();
        try{
            placements = remoteObj.getPlacementsList();
        }catch(RemoteException  e){
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
        }catch(RemoteException e){
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
        }catch(RemoteException  e){
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
        }catch(RemoteException  e){
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
        } catch (RemoteException e) {
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
        } catch (RemoteException e) {
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
        }
    }

    /**
     * This message is sent to the server when the client wants to stop using a toolcard before it ends
     */
    @Override
    public void back() {
        try {
            remoteObj.back();
        } catch (RemoteException e) {
            closeConn();
        }catch (IllegalActionException e){
            if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)) {
                endTurn();
            }
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
            closeConn();
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
            closeConn();
        }
    }

    /**
     * Closes the connection if broken
     */
    private void closeConn(){
        synchronized (lockPing){
            if (connectionOk){
                client.disconnect();
                connectionOk=false;
            }
            lockPing.notifyAll();
        }
    }

    /**
     * If triggered, it means that the connection has broken
     */
    private class ConnectionTimeout extends TimerTask {
        @Override
        public void run(){
            disconnect();
        }
    }

    /**
     * If triggered, it means that the connection has broken
     */
    private void disconnect(){
        synchronized (lockPing) {
            if (connectionOk) {
                connectionOk = false;
                lockPing.notifyAll();
                System.out.println(CONNECTION_TIMEOUT);
                client.disconnect();
            }
            lockPing.notifyAll();
        }
    }

    /**
     * This method provides the ping functionality for checking if the connection is still active
     */
    @Override
    public void pong(){
        new Thread(() -> {
            while(connectionOk) {
                try {
                        pingTimer = new Timer();
                        pingTimer.schedule(new ConnectionTimeout(), PONG_TIME);
                        remoteObj.pong();
                        pingTimer.cancel();
                    try {
                        Thread.sleep(PONG_TIME);
                    } catch (InterruptedException e) {
                        Logger.getGlobal().log(Level.INFO,e.getMessage());
                    }
                } catch (RemoteException e) {
                    disconnect();
                }
            }
        }).start();
    }

    /*
     *  Disabled for rmi
     */
    @Override
    public boolean login(String username, char[] password) {
        return false;
    }

}

