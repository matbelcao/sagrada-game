package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.immutables.LightSchemaCard;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class Board {
    private DraftPool draftPool;
    private ArrayList<Player> players;
    private PubObjectiveCard[] publicObjectives;
    private ToolCard[] toolCards;

    public static final int NUM_OBJECTIVES=3;
    public static final int NUM_TOOLS=3;
    public static final int NUM_ROUNDS=10;
    public static final int NUM_PLAYER_SCHEMAS=4;

    public Board(List<User> users){
        this.publicObjectives=draftPubObjectives();
        this.toolCards=draftToolCards();

        //Instantiating the player's classes
        PrivObjectiveCard [] privObjectiveCards = draftPrivObjectives(users.size());
        players=new ArrayList<>();
        for (int i=0;i<users.size();i++){
            players.add(new Player(users.get(i).getUsername(),i,this,privObjectiveCards[i]));
        }
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

    /**
     * Select random Private Objective Cards
     * @param numPlayers number of cards to extract
     * @return an array containing the private objectives for the match
     */
    private PrivObjectiveCard[] draftPrivObjectives(int numPlayers) {
        PrivObjectiveCard[] privObjectiveCards= new PrivObjectiveCard[numPlayers];
        Random randomGen = new Random();
        for(int i =0; i<numPlayers;i++){
            privObjectiveCards[i]=new PrivObjectiveCard(randomGen.nextInt(PrivObjectiveCard.NUM_PRIV_OBJ) + 1,MasterServer.XML_SOURCE+"PrivObjectiveCard.xml");
        }
        return privObjectiveCards;
    }

    /**
     * Extract the random schema cards to be chosen by users
     * @return an array containing the schema cards
     */
    public SchemaCard[] draftSchemas() {
        SchemaCard[]  shemSchemaCards= new SchemaCard[players.size()*4];
        Random randomGen = new Random();
        for(int i =0; i<players.size()*4;i++){
            shemSchemaCards[i]=new SchemaCard(randomGen.nextInt(SchemaCard.NUM_SCHEMA) + 1,MasterServer.XML_SOURCE+"SchemaCard.xml");
        }
        return shemSchemaCards;
    }

    public Player getPlayer(User user) {
        for(Player p : players){
            if (p.matchesUser(user)){
                return p;
            }
        }
        throw new NoSuchElementException();
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
