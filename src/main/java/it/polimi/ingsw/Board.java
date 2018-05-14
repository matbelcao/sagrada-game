package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private DraftPool draftPool;
    private ArrayList<User> users;
    private PubObjectiveCard [] publicObjectives;
    private ToolCard [] toolCards;
    private int numOfPlayers;

    public static final int NUM_OBJECTIVES=3;
    public static final int NUM_TOOLS=3;
    public static final int NUM_ROUNDS=10;

    public Board(List<User> users,PubObjectiveCard [] publicObjectives,ToolCard [] toolCards){
        this.users= (ArrayList<User>) users;
        this.publicObjectives=publicObjectives;
        this.toolCards=toolCards;
        this.numOfPlayers=this.users.size();
    }

    public List<User> getUsers() {
        return users;
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
