package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private DraftPool draftPool;
    private ArrayList<Player> players;
    private PubObjectiveCard [] publicObjectives;
    private ToolCard [] toolCards;
    private Integer numOfPlayers;


    public List<Player> getPlayers() {
        return players;
    }

    public ToolCard[] getToolCards() {
        return toolCards;
    }

    public PubObjectiveCard[] getPublicObjectives() {
        return publicObjectives;
    }
}
