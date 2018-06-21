package it.polimi.ingsw.common.serializables;

public class LightGameStatus {
    private int numPlayers;
    private int numRound;
    private boolean isFirstTurn;
    private int nowPlaying;
    private boolean isInit;


    public LightGameStatus(boolean isInit,int numPlayers, int numRound, boolean isFirstTurn, int nowPlaying) {
        this.isInit=isInit;
        this.numPlayers = numPlayers;
        this.numRound = numRound;
        this.isFirstTurn = isFirstTurn;
        this.nowPlaying = nowPlaying;
    }

    public boolean isInit() {
        return isInit;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public int getNumRound() {
        return numRound;
    }

    public boolean getIsFirstTurn() {
        return isFirstTurn;
    }

    public int getNowPlaying() {
        return nowPlaying;
    }
}
