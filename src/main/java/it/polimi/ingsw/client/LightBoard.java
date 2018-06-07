package it.polimi.ingsw.client;

import it.polimi.ingsw.common.immutables.*;

import java.util.*;

public class LightBoard extends Observable {
    public static final int NUM_TOOLS=3;
    public static final int NUM_PUB_OBJ=3;
    public static final int MAX_PLAYERS=4;

    private List<LightTool> tools;
    private List<LightCard> pubObj;
    private int numPlayers;
    private LightPrivObj privObj;
    private HashMap<Integer,LightPlayer> players;
    private HashMap<Integer,LightDie> draftPool;
    private List<List<LightDie>> roundTrack;
    private int roundNumber;
    private int nowPlaying;
    private boolean isFirstTurn;
    private int myPlayerId;


    public LightBoard(int numPlayers) {
        this.numPlayers=numPlayers;
        tools=new ArrayList<>();
        pubObj= new ArrayList<>();
        players=new HashMap<>();
        draftPool=new HashMap<>();
        roundTrack= new ArrayList<>();
        nowPlaying=-1;
    }

    public int getMyPlayerId() {
        return myPlayerId;
    }

    public void setMyPlayerId(int myPlayerId) {
        this.myPlayerId = myPlayerId;
    }

    public int getNowPlaying() {
        return nowPlaying;
    }

    public void setNowPlaying(int nowPlaying) {
        this.nowPlaying = nowPlaying;
    }

    public void addPlayer(LightPlayer player){
        assert(players.size()<numPlayers);
        players.put(player.getPlayerId(),player);
    }

    public LightPrivObj getPrivObj() {
        return privObj;
    }

    public void setPrivObj(LightPrivObj privObj) {
        this.privObj = privObj;
    }

    public void addTools(List<LightTool> tool){
        this.tools=tool;
        notifyObservers();
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public List<LightCard> getPubObjs() {
        List<LightCard> clone = new ArrayList<>(pubObj.size());
        for (LightCard item : pubObj) clone.add(item.clone());
        return clone;
    }


    public List<LightTool> getTools() {
        List<LightTool> clone = new ArrayList<>(tools.size());
        for (LightTool item : tools) clone.add(item.clone());
        return clone;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public Map<Integer, LightDie> getDraftPool() {
        return draftPool;
    }

    public void setDraftPool(Map<Integer, LightDie> draftPool) {
        this.draftPool = (HashMap<Integer, LightDie>) draftPool;

    }

    public void setDraftPool(List<LightDie> draftPool) {
        this.draftPool.clear();
        for(int i=0; i<draftPool.size();i++){
            this.draftPool.put(i,draftPool.get(i));

        }

    }

    public List<List<LightDie>> getRoundTrack() {
        return roundTrack;
    }

    public void setRoundTrack(List<List<LightDie>> roundTrack, int numRound) {
        this.roundTrack = roundTrack;
        this.roundNumber=numRound;

    }

    public void addPubObj(LightCard card){
        assert(pubObj.size()<NUM_PUB_OBJ );
        this.pubObj.add(card);
    }

    public void updateSchema(int playerId, LightSchemaCard schema){
        players.get(playerId).setSchema(schema);

    }

    public LightTool getToolByIndex(int index){
        assert(index<tools.size() && index>=0);
        return tools.get(index);
    }

    public LightCard getPubByIndex(int index){
        assert(index<pubObj.size() && index>=0);
        return pubObj.get(index);
    }

    public LightPlayer getPlayerByIndex(int index){
        assert(players.containsKey(index) && players.get(index)!=null);
        return players.get(index);
    }


    public int getRoundNumber() {
        return roundNumber;
    }

    public void setIsFirstTurn(boolean isFirstTurn) {
        this.isFirstTurn = isFirstTurn;
    }

    public boolean getIsFirstTurn() {
        return isFirstTurn;
    }
}
