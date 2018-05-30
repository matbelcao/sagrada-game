package it.polimi.ingsw.client;

import it.polimi.ingsw.common.immutables.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LightBoard {
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

    public LightBoard(int numPlayers) {
        this.numPlayers=numPlayers;
        tools=new ArrayList<>();
        pubObj= new ArrayList<>();
        players=new HashMap<>();
        draftPool=new HashMap<>();
        roundTrack= new ArrayList<>();
    }

    public void addPlayer(LightPlayer player){
        assert(players.size()<numPlayers);
        players.put(player.getPlayerId(),player);
    }

    public LightCard getPrivObj() {
        return privObj;
    }

    public void setPrivObj(LightPrivObj privObj) {
        this.privObj = privObj;
    }

    public void addTool(LightTool tool){
        assert(tools.size()<NUM_TOOLS );
        this.tools.add(tool);
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

    public List<List<LightDie>> getRoundTrack() {
        return roundTrack;
    }

    public void setRoundTrack(List<List<LightDie>> roundTrack) {
        this.roundTrack = roundTrack;
    }

    public void addPubObj(LightTool tool){
        assert(tools.size()<NUM_PUB_OBJ );
        this.tools.add(tool);
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



}
