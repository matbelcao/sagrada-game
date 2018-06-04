package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.connection.User;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class Board {
    private DraftPool draftPool;
    private List<Player> players;
    private PubObjectiveCard[] publicObjectives;
    private ToolCard[] toolCards;
    boolean additionalSchemas;


    public static final int NUM_OBJECTIVES=3;
    public static final int NUM_TOOLS=3;
    public static final int NUM_ROUNDS=10;
    public static final int NUM_PLAYER_SCHEMAS=4;


    public Board(List<User> users, boolean additionalSchemas){
        this.draftPool=new DraftPool();
        this.additionalSchemas=additionalSchemas;
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
        toolCards= new ToolCard[Board.NUM_TOOLS];
        Random randomGen = new Random();
        List<Integer> draftedTools= new ArrayList<>();
        Integer id;
        for(int i =0; i<Board.NUM_TOOLS;i++){
            do{
                id=randomGen.nextInt(ToolCard.NUM_TOOL_CARDS) + 1;
            }while (draftedTools.contains(id));
            draftedTools.add(id);
            toolCards[i]=new ToolCard(id);
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
        Integer id;
        List<Integer> draftedPubs = new ArrayList<>();

        for(int i =0; i<Board.NUM_OBJECTIVES;i++){
            do{
                id=randomGen.nextInt(PubObjectiveCard.NUM_PUB_OBJ) + 1;
            } while(draftedPubs.contains(id));
            draftedPubs.add(id);
            pubObjectiveCards[i]=new PubObjectiveCard(id,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        }
        return pubObjectiveCards;
    }

    /**
     * Select random Private Objective Cards
     * @param numPlayers the number pf players of the match
     * @return an array containing the private objectives for the match
     */
    private PrivObjectiveCard[] draftPrivObjectives(Integer numPlayers) {
        PrivObjectiveCard[] privObjectiveCards= new PrivObjectiveCard[numPlayers];
        Random randomGen = new Random();
        Integer id;
        List<Integer> draftedPrivs=new ArrayList<>();

        for(int i =0; i<numPlayers;i++){
            do{
                id=randomGen.nextInt(PrivObjectiveCard.NUM_PRIV_OBJ) + 1;
            } while(draftedPrivs.contains(id));
            draftedPrivs.add(id);

            privObjectiveCards[i]=new PrivObjectiveCard(id,MasterServer.XML_SOURCE+"PrivObjectiveCard.xml");
        }
        return privObjectiveCards;
    }

    /**
     * Extract the random schema cards to be chosen by users
     * @return an array containing the schema cards
     */
    public SchemaCard[] draftSchemas() {
        SchemaCard[]  schemaChoices= new SchemaCard[NUM_PLAYER_SCHEMAS*players.size()];
        Random randomGen = new Random();
        Integer id;
        List<Integer> draftedSchemas= new ArrayList<>();

        for(int i =0; i < NUM_PLAYER_SCHEMAS*players.size();i++){
            do{
                id=randomGen.nextInt(SchemaCard.NUM_SCHEMA) + 1;
            } while(draftedSchemas.contains(id));

            draftedSchemas.add(id);
            schemaChoices[i]=new SchemaCard(id,MasterServer.XML_SOURCE+"SchemaCard.xml");
        }
        return schemaChoices;
    }

    //To continue.....
    public List<Integer> listSchemaPlacements(User user,int selectedTool, Die selectedDie){
        SchemaCard schema=getPlayer(user).getSchema();
        if(selectedTool!=-1){
            IgnoredConstraint ignored_constraint=toolCards[selectedTool].getIgnoredConstraint();
            return schema.listPossiblePlacements(selectedDie,ignored_constraint);
        }
        return schema.listPossiblePlacements(selectedDie);
    }

    public Player getPlayer(User user) {
        for(Player p : players){
            if (p.matchesUser(user)){
                return p;
            }
        }
        throw new NoSuchElementException();
    }

    public Player getPlayerById(int playerId) {
        for(Player p : players){
            if (p.getGameId()==playerId){
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

    public DraftPool getDraftPool(){
        return this.draftPool;
    }

}
