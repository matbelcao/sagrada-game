package it.polimi.ingsw.client.view;

import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.serializables.*;

import java.util.*;

/**
 * this class is a light version of the board that is in the server its purpose is to only store information that is to be used by
 * the client and ui, the logic of the model is only present on the server
 */
public class LightBoard extends Observable {
    public static final int NUM_TOOLS = 3;
    public static final int NUM_PUB_OBJ = 3;
    public static final int MAX_PLAYERS = 4;
    private List<LightTool> tools;
    private List<LightCard> pubObj;
    private int numPlayers;
    private LightPrivObj privObj;
    private HashMap<Integer, LightPlayer> players;
    private List<LightDie> draftPool;
    private List<List<LightDie>> roundTrack;
    private int roundNumber;
    private int nowPlaying;
    private boolean isFirstTurn;
    private int myPlayerId;
    private List<LightSchemaCard> draftedSchemas;
    private List<Actions> latestOptionsList;
    private List<IndexedCellContent> latestDiceList;
    private List<Integer> latestPlacementsList;
    private IndexedCellContent latestSelectedDie;
    private List<Integer> changes;
    private final Object lockChanges;

    /**
     * this builds the lightboard and initializes the number of players of the match
     *
     * @param numPlayers the number of users that are playing this
     */
    public LightBoard(int numPlayers) {
        this.numPlayers = numPlayers;
        tools = new ArrayList<>();
        pubObj = new ArrayList<>();
        players = new HashMap<>();
        draftPool = new ArrayList<>();
        roundTrack = new ArrayList<>();
        latestOptionsList = new ArrayList<>();
        latestPlacementsList = new ArrayList<>();
        latestDiceList = new ArrayList<>();
        changes = new ArrayList<>();
        lockChanges = new Object();
        nowPlaying = -1;
        roundNumber = -1;
    }

    /**
     * @return the drafted schemas for the initial choice
     */
    public List<LightSchemaCard> getDraftedSchemas() {
        return draftedSchemas;

    }


    /**
     * this adds a new change to the list
     * @param event the new change event
     */
    private void addToChanges(int event) {
        synchronized (lockChanges) {
            changes.add(event);
            lockChanges.notifyAll();
        }
    }

    /**
     * this sets the drafted schemas for the initial choice
     *
     * @param draftedSchemas the drafted schemas
     */
    public void setDraftedSchemas(List<LightSchemaCard> draftedSchemas) {
        this.draftedSchemas = draftedSchemas;
        addToChanges(LightBoardEvents.DRAFTED_SCHEMAS);
        setChanged();
    }

    /**
     * @return the id of the user
     */
    public int getMyPlayerId() {
        return myPlayerId;
    }

    /**
     * sets the id of the user
     *
     * @param myPlayerId the id to be set
     */
    public void setMyPlayerId(int myPlayerId) {
        this.myPlayerId = myPlayerId;
        addToChanges(LightBoardEvents.MY_PLAYER_ID);
        setChanged();
    }

    /**
     * @return the id of the user that is playing his turn at the moment
     */
    public int getNowPlaying() {
        return nowPlaying;
    }

    /**
     * sets the id of the user that is now playing his turn
     *
     * @param nowPlaying the id
     */
    public void setNowPlaying(int nowPlaying) {
        this.nowPlaying = nowPlaying;
        addToChanges(LightBoardEvents.NOW_PLAYING);
        setChanged();
    }

    /**
     * this adds a player to the board
     *
     * @param player the player to be added
     */
    public void addPlayer(LightPlayer player) {
        assert (players.size() < numPlayers);
        players.put(player.getPlayerId(), player);
        setChanged();
    }

    /**
     * @return the private objective of the player
     */
    public LightPrivObj getPrivObj() {
        return privObj;
    }

    /**
     * sets the private objective of the player
     *
     * @param privObj the private objective
     */
    public void setPrivObj(LightPrivObj privObj) {
        this.privObj = privObj;
        addToChanges(LightBoardEvents.PRIV_OBJ);
        setChanged();
    }

    /**
     * this sets the list of tools for the match
     *
     * @param tools the list of tools
     */
    public void setTools(List<LightTool> tools) {
        this.tools = tools;
        addToChanges(LightBoardEvents.TOOLS);
        setChanged();
    }

    /**
     * this sets ne public objectives
     *
     * @param pubObjs the public objectives
     */
    public void setPubObjs(List<LightCard> pubObjs) {
        this.pubObj = pubObjs;
        addToChanges(LightBoardEvents.PUB_OBJ);
        setChanged();
    }

    /**
     * @return the list of public objectives for the match
     */
    public List<LightCard> getPubObjs() {
        return pubObj;
    }

    /**
     * @return the list of tools
     */
    public List<LightTool> getTools() {
        return tools;
    }

    /**
     * @return the number of players of the match
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * @return the draftpool
     */
    public List<LightDie> getDraftPool() {
        return draftPool;
    }


    /**
     * this updates the draftpool by overwriting it
     *
     * @param draftPool the new draftpool
     */
    public void setDraftPool(List<LightDie> draftPool) {
        this.draftPool = draftPool;
        addToChanges(LightBoardEvents.DRAFT_POOL);
        setChanged();

    }

    /**
     * @return the roundtrack
     */
    public List<List<LightDie>> getRoundTrack() {
        return roundTrack;
    }

    /**
     * sets the roundtrack and number of round
     *  @param roundTrack the new roundtrack
     *
     */
    public void setRoundTrack(List<List<LightDie>> roundTrack) {
        this.roundTrack = roundTrack;
        addToChanges(LightBoardEvents.ROUND_TRACK);
        setChanged();
    }

    /**
     * this sets the new round number
     * @param roundNumber the new round number
     */
    public void setRoundNumber(int roundNumber) {

        this.roundNumber = roundNumber;
        addToChanges(LightBoardEvents.ROUND_NUMBER);
        setChanged();
    }

    /**
     * this updates the schema of a certain player
     * @param playerId the id of said player
     * @param schema the new schema to be set
     */
    public void updateSchema(int playerId, LightSchemaCard schema){
        players.get(playerId).setSchema(schema);
        addToChanges(LightBoardEvents.SCHEMA);
        setChanged();
    }

    /**
     * this updates the favor tokens of a certain player
     * @param playerId the id of said player
     * @param favorTokens the remaining favor tokens
     */
    public void updateFavorTokens(int playerId, int favorTokens){
        players.get(playerId).setFavorTokens(favorTokens);
        addToChanges(LightBoardEvents.FAVOR_TOKENS);
        setChanged();
    }

    /**
     * this updates the status of a certain player
     * @param playerId the id of said player
     * @param status the new status
     */
    public void updateStatus(int playerId, LightPlayerStatus status){
        players.get(playerId).setStatus(status);
        addToChanges(LightBoardEvents.STATUS);
        setChanged();
    }



    /**
     * this returns a player of a certain id
     * @param playerId  the id of the desired player
     * @return the player
     */
    public LightPlayer getPlayerById(int playerId){
        assert(players.containsKey(playerId) && players.get(playerId)!=null);
        return players.get(playerId);
    }


    /**
     * @return the number of the round
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * sets true iff this is the first turn in the round played by the player that has nowPlaying as id
     * @param isFirstTurn the new value for this flag
     */
    public void setIsFirstTurn(boolean isFirstTurn) {
        this.isFirstTurn = isFirstTurn;
        addToChanges(LightBoardEvents.IS_FIRST_TURN);
        setChanged();
    }


    /**
     * this tells the lightboard that the fsm state has changed
     */
    public void stateChanged(){
        addToChanges(LightBoardEvents.STATE_CHANGED);
        setChanged();
    }
    /**
     * @return the isFirstTurn value
     */
    public boolean getIsFirstTurn() {
        return isFirstTurn;
    }

    /**
     * this sets a list of commands
     * @param optionsList the new list of options
     */
    public void setLatestOptionsList(List<Actions> optionsList) {
        this.latestOptionsList = optionsList;
        addToChanges(LightBoardEvents.OPTION);
        setChanged();
    }

    /**
     * @return the last set list of options
     */
    public List<Actions> getLatestOptionsList() {
        return latestOptionsList;
    }

    /**
     * @return the latest list of dice that was set
     */
    public List<IndexedCellContent> getLatestDiceList() {
        return latestDiceList;
    }

    /**
     * this sets the new dice list
     * @param latestDiceList the new dice list
     */
    public void setLatestDiceList(List<IndexedCellContent> latestDiceList) {
        this.latestDiceList = latestDiceList;
        addToChanges(LightBoardEvents.DICE_LIST);
        setChanged();
    }

    /**
     * this sets the new placements list
     * @param placementsList the new placements list
     */
    public void setLatestPlacementsList(List<Integer> placementsList) {
        this.latestPlacementsList = placementsList;
        addToChanges(LightBoardEvents.PLACEMENTS_LIST);
        setChanged();
    }

    /**
     * @return the latest placements list that was set
     */
    public List<Integer> getLatestPlacementsList() {
        return latestPlacementsList;
    }

    /**
     * this sets the new selected die
     * @param latestSelectedDie the newly selected die
     */
    public void setLatestSelectedDie(IndexedCellContent  latestSelectedDie) {
        this.latestSelectedDie = latestSelectedDie;
        addToChanges(LightBoardEvents.SELECT_DIE);
        setChanged();
    }

    /**
     * @return a copy of the changes and clears it
     */
    public  List<Integer> getChanges() {
        List<Integer> copy;
        synchronized (lockChanges) {
            copy = new ArrayList<>(changes);

            changes=new ArrayList<>();
            lockChanges.notifyAll();
        }
        return copy;
    }


    /**
     * @return the latest die that was selected
     */
    public IndexedCellContent getLatestSelectedDie() {
        return latestSelectedDie;
    }

    public void resetLatests() {
        latestDiceList=new ArrayList<>();
        latestOptionsList=new ArrayList<>();
        latestPlacementsList=new ArrayList<>();
    }

    /**
     * returns the player in the passed position
     * @param pos the position
     * @return the player in this position
     */
    public LightPlayer getByFinalPosition(int pos){
        if (pos<=0 || pos>numPlayers){
            throw new IllegalArgumentException();
        }

        for(int id=0;id<numPlayers;id++){
            if(getPlayerById(id).getFinalPosition()==pos){
                return getPlayerById(id);
            }
        }
        throw new IllegalArgumentException();
    }

}
