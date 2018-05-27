package it.polimi.ingsw.client;

import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightDie;
import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.common.immutables.LightTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LightBoard {
    public static final int NUM_TOOLS=3;
    public static final int NUM_PUB_OBJ=3;
    public static final int MAX_PLAYERS=4;

    private ArrayList<LightTool> tools;
    private ArrayList<LightCard> pubObj;
    private LightCard privObj;
    private HashMap<Integer,LightPlayer> players;
    private HashMap<Integer,LightDie> draftPool;
    private HashMap<Integer,LightDie> roundTrack;

    public LightBoard() {
        tools=new ArrayList<>();
        pubObj= new ArrayList<>();
        players=new HashMap<>();
        draftPool=new HashMap<>();
        roundTrack= new HashMap<>();
    }

    public void addPlayer(LightPlayer player){
        assert(players.size()<MAX_PLAYERS);
        players.put(player.getPlayerId(),player);
    }

    public LightCard getPrivObj() {
        return privObj;
    }

    public void setPrivObj(LightCard privObj) {
        this.privObj = privObj;
    }

    public void addTool(LightTool tool){
        assert(tools.size()<NUM_TOOLS );
        this.tools.add(tool);
    }

    public Map<Integer, LightDie> getDraftPool() {
        return draftPool;
    }

    public void setDraftPool(Map<Integer, LightDie> draftPool) {
        this.draftPool = (HashMap<Integer, LightDie>) draftPool;
    }

    public Map<Integer, LightDie> getRoundTrack() {
        return roundTrack;
    }

    public void setRoundTrack(Map<Integer, LightDie> roundTrack) {
        this.roundTrack = (HashMap<Integer, LightDie>) roundTrack;
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
