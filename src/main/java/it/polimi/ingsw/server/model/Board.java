package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.connection.MasterServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private DraftPool draftPool;
    private ArrayList<User> users;
    private PubObjectiveCard[] publicObjectives;
    private ToolCard[] toolCards;
    private int numOfPlayers;

    public static final int NUM_OBJECTIVES=3;
    public static final int NUM_TOOLS=3;
    public static final int NUM_ROUNDS=10;

    public Board(List<User> users){
        this.users= (ArrayList<User>) users;
        this.publicObjectives=publicObjectives;
        this.toolCards=toolCards;
        this.numOfPlayers=this.users.size();
    }

    /**
     * Selects random ToolCards to be used in the match
     * @return an array containing the tools
     */
    private ToolCard[] draftToolCards() {
        ToolCard[] toolCards= new ToolCard[Board.NUM_TOOLS];
        Random randomGen = new Random();
        for(int i =0; i<Board.NUM_TOOLS;i++){
            toolCards[i]=new ToolCard(randomGen.nextInt(ToolCard.NUM_TOOL_CARDS) + 1, MasterServer.XML_SOURCE+"ToolCard.xml");
        }
        return toolCards;
    }

    /**
     * Selects random Public Objective Cards
     * @return an array containing the public objectives for the match
     */
    private PubObjectiveCard[] draftPubObjectives() {
        PubObjectiveCard[] pubObjectiveCards= new PubObjectiveCard[Board.NUM_OBJECTIVES];
        Random randomGen = new Random();
        for(int i =0; i<Board.NUM_OBJECTIVES;i++){
            pubObjectiveCards[i]=new PubObjectiveCard(randomGen.nextInt(PubObjectiveCard.NUM_PUB_OBJ) + 1,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        }
        return pubObjectiveCards;
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
