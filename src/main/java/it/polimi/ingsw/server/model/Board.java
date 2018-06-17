package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
import sun.net.www.content.text.plain;

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
        /*Random randomGen = new Random();
        List<Integer> draftedTools= new ArrayList<>();
        Integer id;
        for(int i =0; i<Board.NUM_TOOLS;i++){
            do{
                id=randomGen.nextInt(ToolCard.NUM_TOOL_CARDS) + 1;
            }while (draftedTools.contains(id));
            draftedTools.add(id);
            toolCards[i]=new ToolCard(id);
        }*/
        toolCards[0]=new ToolCard(10);
        toolCards[1]=new ToolCard(11);
        toolCards[2]=new ToolCard(12);
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
            schemaChoices[i]=new SchemaCard(id);
        }
        return schemaChoices;
    }

    public List<IndexedCellContent> indexedDiceList(int playerId,Place from, Color constraint){
        switch(from) {
            case SCHEMA:
                return indexedSchemaDiceList(playerId,constraint);
            case DRAFTPOOL:
                return indexedDraftpoolDiceList();
            case ROUNDTRACK:
                return indexedRoundTrackDiceList();
            default:
                throw  new IllegalArgumentException();
        }
    }

    private  List<IndexedCellContent> indexedSchemaDiceList(int playerId, Color constraint){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        SchemaCard schema= getPlayerById(playerId).getSchema();
        Die die;

        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()) {
            die = diceIterator.next().getDie();
            if(!constraint.equals(Color.NONE)){
                if(die.getColor().equals(constraint)){
                    indexedCell = new IndexedCellContent(diceIterator.getIndex(),Place.SCHEMA, die);
                    indexedList.add(indexedCell);
                }
            }else{
                indexedCell = new IndexedCellContent(diceIterator.getIndex(),Place.SCHEMA, die);
                indexedList.add(indexedCell);
            }
        }
        return indexedList;
    }

    private List<IndexedCellContent> indexedDraftpoolDiceList(){
        List<Die> draftedDice=getDraftPool().getDraftedDice();
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        Die die;

        for (int index=0;index<draftedDice.size();index++){
            die=draftedDice.get(index);
            indexedCell=new IndexedCellContent(index,Place.DRAFTPOOL,die);
            indexedList.add(indexedCell);
        }
        return indexedList;
    }

    private List<IndexedCellContent> indexedRoundTrackDiceList(){
        List<List<Die>> dieTrack=getDraftPool().getRoundTrack().getTrack();
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;

        for(int index=0;index<dieTrack.size();index++){
            List<Die> dieList= dieTrack.get(index);
            for(Die d:dieList){
                indexedCell=new IndexedCellContent(index,Place.ROUNDTRACK,d);
                indexedList.add(indexedCell);
            }
        }
        return indexedList;
    }

    /**
     * Selects the die from a draftpool/user's schema card/roundtrack a returns the schema card's possible placements
     * @param user the user who made the request
     * @return the list of possible placements in the user's schema card
     */
    public Die selectDie(int playerId,Place from, int die_index,Color constraint) {

        switch (from){
            case SCHEMA:
                return getPlayerById(playerId).getSchema().getSchemaDiceList(constraint).get(die_index);
            case DRAFTPOOL:
                return getDraftPool().getDraftedDice().get(die_index);
            case ROUNDTRACK:
                List<Die> trackList = getDraftPool().getRoundTrack().getTrackList();
                return trackList.get(die_index);
        }
        return null;
    }

    /**
     * Return the real position (0 to 19) for schema
     * @param playerId
     * @param from
     * @param die
     * @return
     */
    public int getDiePosition(int playerId, Place from, Die die){
        switch (from){
            case SCHEMA:
                return getPlayerById(playerId).getSchema().getDiePosition(die);
            case DRAFTPOOL:
                return getDraftPool().getDraftedDice().indexOf(die);
            case ROUNDTRACK:
                return getDraftPool().getRoundTrack().getTrackList().indexOf(die);
            default:
                return -1;
        }
    }

    //only for
    public boolean schemaPlacement(int playerId,int newIndex,int oldIndex, Die selectedDie, IgnoredConstraint constraint){
        SchemaCard schemaCard=getPlayerById(playerId).getSchema();
        List<Integer> placements= schemaCard.listPossiblePlacements(selectedDie,constraint);
        try {
            schemaCard.putDie(placements.get(newIndex),selectedDie,constraint);
            getDraftPool().removeDie(oldIndex);
            return true;
        } catch (IllegalDieException e) {
            return false;
        }
    }

    public List<Integer> listSchemaPlacements(int playerId, Die selectedDie, IgnoredConstraint constraint){
        SchemaCard schema = getPlayerById(playerId).getSchema();
        return schema.listPossiblePlacements(selectedDie,constraint);
    }


    public void removeOldDice(int playerId, Place from, List<Integer> oldIndexes){
        for (Integer index: oldIndexes){
            switch (from){
                case SCHEMA:
                    getPlayerById(playerId).getSchema().removeDie(index);
                    break;
                case DRAFTPOOL:
                    getDraftPool().removeDie(index);
                    break;
                case ROUNDTRACK:
                    getDraftPool().getRoundTrack().removeDie(index);
                    break;
                default:
                    return;
            }
        }
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
