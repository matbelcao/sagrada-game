package it.polimi.ingsw.common.serializables;

import java.io.Serializable;

public class LightGameStatus implements Serializable {
    private int numPlayers;
    private int numRound;
    private boolean isFirstTurn;
    private int nowPlaying;
    private boolean isInit;


    public LightGameStatus(boolean isInit,int numPlayers, int numRound, boolean isFirstTurn, int nowPlaying) {
        this.isInit = isInit;
        this.numPlayers = numPlayers;
        this.numRound = numRound;
        this.isFirstTurn = isFirstTurn;
        this.nowPlaying = nowPlaying;
    }

    /**
     * @return true iff the game
     */
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
