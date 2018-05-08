package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private DraftPool draftPool;
    private ArrayList<Player> players;
    private PubObjectiveCard [] publicObjectives;
    private ToolCard [] toolCards;
    private Integer numOfPlayers;

    public static final int NUM_OBJECTIVES=3;
    public static final int NUM_TOOLS=3;

    public List<Player> getPlayers() {
        return players;
    }

    public ToolCard getToolCard(int index) {
        assert (index>=0 && index<NUM_TOOLS);
        return toolCards[index];
    }

    public PubObjectiveCard getPublicObjective(int index) {
        assert (index>=0 && index<NUM_OBJECTIVES);
        return publicObjectives[index];
    }
}
