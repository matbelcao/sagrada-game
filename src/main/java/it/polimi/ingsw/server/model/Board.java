package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.enums.Turn;
import it.polimi.ingsw.common.serializables.IndexedCellContent;
import it.polimi.ingsw.common.serializables.LightPlayer;
import it.polimi.ingsw.common.serializables.RankingEntry;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.enums.ServerState;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * This class implements the game logic useful to place the dice in the SchemaCard or execute a specific ToolCard.
 * It also provides the methods for the random extraction of SchemaCards, ToolCards, Public and Private Objectives cards.
 */
public class Board {
    public static final int NUM_OBJECTIVES=3;
    public static final int NUM_TOOLS=3;
    public static final int NUM_ROUNDS=10;
    public static final int NUM_PLAYER_SCHEMAS=4;

    private DraftPool draftPool;
    private List<Player> players;
    private PubObjectiveCard[] publicObjectives;
    private ToolCard[] toolCards;
    private SchemaCard[]  schemaDrafted;
    private boolean additionalSchema;
    private ServerFSM fsm;
    private ServerState status;

    private ToolCard selectedTool;
    private Die selectedDie;
    private int oldIndex;
    private Commands selectedCommand;
    private Boolean enableToolList;
    private List<IndexedCellContent> diceList;
    private List<Commands> commandsList;
    private List<Integer> placements;

    /**
     * The class constructor. Initializes the variables and extracts the objects to distribute to the players
     * @param users the List of users that are playing in the match
     * @param additionalSchema true if the additional schema FA is enabled
     */
    public Board(List<User> users, boolean additionalSchema){
        this.draftPool=new DraftPool();
        this.additionalSchema=additionalSchema;
        this.publicObjectives=draftPubObjectives();
        this.toolCards=draftToolCards();
        status=ServerState.INIT;
        fsm=new ServerFSM();

        //Instantiating the player's classes
        PrivObjectiveCard [] privObjectiveCards = draftPrivObjectives(users.size());
        players=new ArrayList<>();
        for (int i=0;i<users.size();i++){
            players.add(new Player(users.get(i).getUsername(),i,this,privObjectiveCards[i]));
        }
    }

    //GAME end {<player_id>,<final_score>,<position>}

    /**
     * Selects the random ToolCards to be used in the match
     * @return an array containing the ToolCards
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
        /*toolCards[0]=new ToolCard(2);
        toolCards[1]=new ToolCard(6);
        toolCards[2]=new ToolCard(7);*/
        return toolCards;
    }

    /**
     * Selects the random PublicObjectiveCards
     * @return an array containing the PublicObjectiveCards for the match
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
     * Select the random PrivateObjectiveCards
     * @param numPlayers the number of users that are playing in the match
     * @return an array containing the PrivateObjectiveCards for the match
     */
    private PrivObjectiveCard[] draftPrivObjectives(int numPlayers) {
        PrivObjectiveCard[] privObjectiveCards= new PrivObjectiveCard[numPlayers];
        Random randomGen = new Random();
        Integer id;
        List<Integer> draftedPrivs=new ArrayList<>();

        for(int i =0; i<numPlayers;i++){
            do{
                id=randomGen.nextInt(PrivObjectiveCard.NUM_PRIV_OBJ) + 1;
            } while(draftedPrivs.contains(id));
            draftedPrivs.add(id);

            privObjectiveCards[i]=new PrivObjectiveCard(id);
        }
        return privObjectiveCards;
    }

    /**
     * Extracts the random SchemaCards to be chosen by users
     * @return an array containing the SchemaCards
     */
    public SchemaCard[] draftSchemas() {
        schemaDrafted= new SchemaCard[NUM_PLAYER_SCHEMAS*players.size()];
        Random randomGen = new Random();
        Integer id;
        List<Integer> draftedIndex= new ArrayList<>();
        int offset=0;
        int i=0;
        int p=0;

        if(additionalSchema){
            int numAdditionalSchemas=SchemaCard.getAdditionalSchemaSize();

            if(numAdditionalSchemas>NUM_PLAYER_SCHEMAS*players.size()){
                numAdditionalSchemas=NUM_PLAYER_SCHEMAS*players.size();
            }

            offset=numAdditionalSchemas/players.size();

            schemaDrafted=draftAdditionalSchemas(offset);

        }

        for(p=0;p<players.size();p++) {
            for (i = 0; i < (NUM_PLAYER_SCHEMAS-offset); i++) {
                do {
                    id = randomGen.nextInt(SchemaCard.NUM_SCHEMA) + 1;
                } while (draftedIndex.contains(id));

                draftedIndex.add(id);
                schemaDrafted[(p*NUM_PLAYER_SCHEMAS)+i+offset] = new SchemaCard(id, false);
            }
        }

        System.out.println("--------------------");
        for(SchemaCard s: schemaDrafted){
            System.out.println(s.getName());
        }

        return schemaDrafted;
    }

    /**
     * Extracts the personalized SchemaCards and puts them into an array
     * @param quantity the number of cards to extract
     * @return the Array containig the additional SchemaCards extracted
     */
    private SchemaCard[] draftAdditionalSchemas(int quantity){
        Random randomGen = new Random();
        Integer id;
        List<Integer> draftedIndex= new ArrayList<>();
        List<SchemaCard> additionalSchemas=new ArrayList<>();

        if(quantity>0) {
            for(int i=0;i<quantity*players.size();i++){
                do{
                    id=randomGen.nextInt(quantity*players.size()) + 1;
                } while(draftedIndex.contains(id));
                draftedIndex.add(id);
                additionalSchemas.add(i,new SchemaCard(id,true));
            }

            for(int p=0;p<players.size();p++){
                for(int i=0;i<quantity;i++){
                    schemaDrafted[(p*NUM_PLAYER_SCHEMAS)+i]=additionalSchemas.get(0);
                    additionalSchemas.remove(0);
                }
            }
        }
        return schemaDrafted;
    }

    /**
     * Responds to the request by sending the player-specific schema card. If the toolcard execution is enabled, it will
     * be sent the temporary schema card used during the tool-specific execution flow.
     * @param playerId the id of the player's desired schema card
     * @return the requested card
     */
    public SchemaCard getUserSchemaCard(int playerId) {
        if(fsm.isToolActive()){
            return selectedTool.getNewSchema();
        }else {
            return getPlayerById(playerId).getSchema();
        }
    }

    /**
     * Returns to the User who made the request an indexed List of dice contained in a specific board position.
     * The selection of the interested area is automated by the logic.
     * @return the indexed List of dice contained in a specific board position
     */
    public List<IndexedCellContent> getDiceList(){
        Color constraint = Color.NONE;

        if(fsm.isToolActive()){
            constraint=selectedTool.getColorConstraint();
            if(selectedTool.isInternalSchemaPlacement()){
                diceList=selectedTool.internalIndexedSchemaDiceList();
                if(!selectedTool.isSetColorFromRountrackCard() || enableToolList ){
                    enableToolList=true;
                }
            }else if(enableToolList && diceList.isEmpty()){ //skip if it's not required to select a die (ALL option)
                status=fsm.endTool();
                return diceList;
            }
        }

        if(!enableToolList){
            switch(fsm.getPlaceFrom()) {
                case SCHEMA:
                    diceList = indexedSchemaDiceList(constraint);
                    break;
                case DRAFTPOOL:
                    diceList = indexedDraftpoolDiceList();
                    break;
                case ROUNDTRACK:
                    diceList = indexedRoundTrackDiceList();
                    break;
                default:
                    throw  new IllegalArgumentException();
            }
        }

        if(status.equals(ServerState.MAIN)){
            status=fsm.nextState(selectedCommand);
        }
        status=fsm.nextState(selectedCommand);
        return diceList;
    }

    /**
     * Allows the player to select a die from a previously sent list.
     * Returns to the User who made the request an indexed List of commands that can be executed on a certain selected die.
     * @param dieIndex the index of the previously indexed dice List sent to the client
     * @return the indexed List of commands that can be executed
     */
    public List<Commands> selectDie(int dieIndex){

        Color constraint = Color.NONE;

        if(fsm.isToolActive()) {
            //toolcard enabled
            constraint = selectedTool.getColorConstraint();
            if(selectedTool.isInternalSchemaPlacement() && enableToolList){
                selectedDie=selectedTool.internalSelectDie(dieIndex);
            }else if (selectedCommand.equals(Commands.INCREASE_DECREASE) || selectedCommand.equals(Commands.SET_SHADE)){
                selectedDie.setColor(diceList.get(dieIndex).getContent().getColor().toString());
                selectedDie.setShade(diceList.get(dieIndex).getContent().getShade().toInt());
            } else{
                selectedDie = getDieSelected(dieIndex, constraint);
                oldIndex = getDiePosition(selectedDie);
                selectedTool.selectDie(selectedDie);
            }
            commandsList = selectedTool.getActions();
        }else {
            //Toolcard disabled
            selectedDie = getDieSelected(dieIndex, constraint);
            oldIndex = getDiePosition(selectedDie);
            commandsList=new ArrayList<>();
            commandsList.add(Commands.PLACE_DIE);
        }

        status=fsm.nextState(selectedCommand);
        enableToolList =false;
        return commandsList;
    }

    /**
     * Sets the chosen schema card to the user's relative player instance, if all the player have choose a schema card
     * the timer will be stopped
     * @param user the user to set the card
     * @param schemaIndex the index of the schema card (for each player (0 to 3)
     * @return true iff the operation was successful
     */
    public boolean chooseSchemaCard(User user,int schemaIndex){
        boolean response;
        if(schemaIndex<0 || schemaIndex >=4){return false;}
        response=getPlayer(user).setSchema(schemaDrafted[(getPlayer(user).getGameId()*Board.NUM_PLAYER_SCHEMAS)+schemaIndex]);
        return response;
    }

    /**
     * Selects the command option to run and sets the class variables according to the specific case
     * @param index the index of the option to select
     * @return true iff the operation was successful
     */
    public boolean chooseOption(int index){
        boolean response=true;
        if(commandsList.size()<=index){return false;}
        selectedCommand=commandsList.get(index);
        if(fsm.isToolActive()){
            switch (selectedCommand){
                case INCREASE_DECREASE:
                    diceList=selectedTool.shadeIncreaseDecrease(selectedDie);
                    enableToolList =true;
                    response=diceList != null;
                    break;
                case SWAP:
                    fsm.setPlaceFrom(selectedTool.getPlaceTo());
                    response= selectedTool.swapDie();
                    break;
                case REROLL:
                    diceList=selectedTool.rerollDie();
                    enableToolList =true;
                    response=diceList != null;
                    break;
                case FLIP:
                    diceList=selectedTool.flipDie();
                    response=diceList != null;
                    break;
                case SET_SHADE:
                    fsm.setPlaceFrom(Place.DICEBAG);
                    selectedDie=getDraftPool().putInBagAndExtract(selectedDie);
                    diceList=selectedTool.chooseShade();
                    enableToolList =true;
                    response=diceList != null;
                    break;
                case SET_COLOR:
                    selectedTool.setColor();
                    enableToolList =true;
                    fsm.setPlaceFrom(selectedTool.getPlaceFrom());
                    response= true;
                    break;
                case PLACE_DIE:
                    response= true;
                    break;
                case NONE:
                    response= true;
                    break;
                default:
                    response= false;
            }
        }

        if(response){
            status=fsm.nextState(selectedCommand);
        }
        return response;
    }

    /**
     * Place the die in the desired cell, which is selected with the index parameter from the list of possible placements
     * previously sent to the client
     * @param index the index of the cell in the prevoisly index List sent to the client
     * @return true iff the operation was successful
     */
    public boolean choosePlacement(int index){
        if(placements.size()<=index || !selectedCommand.equals(Commands.PLACE_DIE) || selectedDie==null){return false;}
        boolean response;
        IgnoredConstraint constraint;

        if(fsm.isToolActive()){
            if(selectedTool.isInternalSchemaPlacement()){
                response=selectedTool.internalDiePlacement(index);
            }else{
                constraint = selectedTool.getIgnoredConstraint();
                response=schemaPlacement(index,constraint);
                fsm.placeDie();
            }
        }else{
            constraint=IgnoredConstraint.NONE;
            response=schemaPlacement(index,constraint);
            fsm.placeDie();
        }

        if(response){
            status=fsm.nextState(selectedCommand);
        }
        return response;
    }

    /**
     * Returns to the User who made the request the list of possible placements if the selected action is PLACE_DIE.
     * The composition of the list, according to the various Constraints/ToolCards enabled, is automated.
     * @return the list of possible placements, ordered in increasing order
     */
    public List<Integer> getPlacements(){
        IgnoredConstraint constraint;
        SchemaCard schema = getPlayerById(fsm.getUserPlayingId()).getSchema();

        if(fsm.isToolActive()) {
            if(selectedTool.isInternalSchemaPlacement()){
                placements=selectedTool.internalListPlacements();
            }else{
                constraint = selectedTool.getIgnoredConstraint();
                placements = schema.listPossiblePlacements(selectedDie,constraint);
            }

        }else{
            constraint=IgnoredConstraint.NONE;
            placements = schema.listPossiblePlacements(selectedDie,constraint);
        }


        status=fsm.nextState(selectedCommand);
        return placements;
    }

    /**
     * Returns to the User who made the request the affirmative or negative answer to attempting to enable the selected
     * tool card. If the response is affirmative, the method will trigger the client's update requests.
     * @param index the index of the previously indexed List of tool cards sent to the client (0 to 2)
     * @param turn if the turn is the first or the second of the round
     * @param roundNumber the number of the round
     * @return true if the ToolCard is successfully enabled
     */
    public boolean activeTool(int index,Turn turn,int roundNumber) {
        Player player=getPlayerById(fsm.getUserPlayingId());

        Boolean toolEnabled=getToolCard(index).enableToolCard(player,roundNumber,turn,fsm.getNumDiePlaced(),player.getSchema());
        if(toolEnabled){
            selectedTool=getToolCard(index);
            status=fsm.newToolUsage(selectedTool);
            if(selectedTool.isRerollAllDiceCard()){
                List<Die> dielist;
                switch (selectedTool.getPlaceFrom()){
                    case DRAFTPOOL:
                        dielist=getDraftPool().getDraftedDice();
                        break;
                    case ROUNDTRACK:
                        dielist=getDraftPool().getRoundTrack().getTrackList();
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                selectedTool.rerollAll(dielist);
                diceList=new ArrayList<>();
                enableToolList =true;
            }else if(selectedTool.isSetColorFromRountrackCard()){
                fsm.setPlaceFrom(Place.ROUNDTRACK);
                enableToolList=false;
            }
        }else{
            exit();
        }
        return toolEnabled;

    }

    /**
     * Returns to the user who made the request the ToolCard status.
     * A negative answer indicates that the execution of the action flow of the toolcard has ended.
     * @return true if the execution flow is not ended, false otherwise
     */
    public boolean toolStatus() {
        if(!selectedTool.toolCanContinue(getPlayerById(fsm.getUserPlayingId()))){
            List<Integer> oldIndexes=selectedTool.getOldIndexes();
            for (Integer index: oldIndexes){
                switch (fsm.getPlaceFrom()){
                    case SCHEMA:
                        getPlayerById(fsm.getUserPlayingId()).getSchema().removeDie(index);
                        break;
                    case DRAFTPOOL:
                        getDraftPool().removeDie(index);
                        break;
                    case ROUNDTRACK:
                        getDraftPool().getRoundTrack().removeDie(index);
                        break;
                    default:
                }
            }
            exit();
        }else{
            status=fsm.nextState(selectedCommand);
        }
        return fsm.isToolActive();
    }

    /**
     * Allows the User to not perform a placement (with the current selected die) and select a new one without interrupting
     * the execution of a multiple-message command.
     */
    public void discard(){
        if(fsm.isToolActive()){
            selectedTool.toolDiscard();
        }
        status=fsm.fsmDiscard();
        selectedDie=null;
    }

    /**
     * Allows the User to interrupt a multiple-message command (for COMPLEX ACTIONS like die placements, ToolCard usages, ecc)
     */
    public void exit(){
        if(fsm.isToolActive()){
            selectedTool.toolExit(getPlayerById(fsm.getUserPlayingId()));
        }
        status=fsm.fsmExit();
        enableToolList =false;
        selectedTool=null;
        selectedDie=null;
        diceList=new ArrayList<>();
        selectedCommand=Commands.NONE;
    }

    /**
     * Calculates the players scores and returns the ranking inside the LightPlayer object
     * @return the list of players in the match with the updated ranks
     */
    public List<RankingEntry> gameRunningEnd(){
        List<RankingEntry> playerScores=new ArrayList<>();
        int maxScore=0;
        int position=1;

        for(Player p:players){
            if(!p.hasQuitted()){
                p.calculateScore();
            }
        }

        for(int i=0; i<players.size();i++){
            for(Player p2: players){
                if(p2.getScore()>maxScore && p2.getFinalPosition()==0){
                    maxScore=p2.getScore();
                }
            }
            for (Player p1: players){
                if(p1.getScore()==maxScore){
                    p1.setFinalPosition(position);
                }
            }
            maxScore=0;
            position++;
        }

        for(Player p:players) {
            playerScores.add(new RankingEntry(p.getGameId(),p.getScore(),p.getFinalPosition()));
        }

        return playerScores;
    }

    public List<RankingEntry> gameInitEnd(){
        List<RankingEntry> playerScores=new ArrayList<>();
        LightPlayer player;

        for(Player p:players){

            playerScores.add(new RankingEntry(p.getGameId(),p.getScore(),p.getFinalPosition()));
        }

        return playerScores;
    }



    /**
     * Returns and indexed List of the dice contained in the player's SchemaCard
     * @param constraint the constraint to select dice of only one color
     * @return the indexed List of the dice contained in the SchemaCard
     */
    private  List<IndexedCellContent> indexedSchemaDiceList(Color constraint){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        SchemaCard schema= getPlayerById(fsm.getUserPlayingId()).getSchema();
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

    /**
     * Returns and indexed List of the dice contained in the DraftPool
     * @return the indexed List of the dice contained in the DraftPool
     */
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

    /**
     * Returns and indexed List of the dice contained in the RoundTrack
     * @return the indexed List of the dice contained in the RoundTrack
     */
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
     * Selects the die from the DraftPool/user's SchemaCard/RoundTrack and returns it.
     * @return the die selected
     */
    private Die getDieSelected(int dieIndex,Color constraint) {
        switch (fsm.getPlaceFrom()){
            case SCHEMA:
                return getPlayerById(fsm.getUserPlayingId()).getSchema().getSchemaDiceList(constraint).get(dieIndex);
            case DRAFTPOOL:
                return getDraftPool().getDraftedDice().get(dieIndex);
            case ROUNDTRACK:
                List<Die> trackList = getDraftPool().getRoundTrack().getTrackList();
                return trackList.get(dieIndex);
            default:
                return null;
        }
    }

    /**
     * Retrieves the die's physical posizion from the DraftPool/user's SchemaCard/RoundTrack and returns it.
     * @param die the die to find the position
     * @return the die's position
     */
    private int getDiePosition(Die die){
        switch (fsm.getPlaceFrom()){
            case SCHEMA:
                return getPlayerById(fsm.getUserPlayingId()).getSchema().getDiePosition(die);
            case DRAFTPOOL:
                return getDraftPool().getDraftedDice().indexOf(die);
            case ROUNDTRACK:
                return getDraftPool().getRoundTrack().getTrackList().indexOf(die);
            default:
                return -1;
        }
    }


    /**
     * Places the die in the SchemaCard and then removes it from the old position, it can be used only for external
     * placements (!SchemaCard->SchemaCard)
     * @param newIndex the index to place the die
     * @param constraint the color restriction, if enabled
     * @return true if the placement was completed successfully
     */
    private boolean schemaPlacement(int newIndex, IgnoredConstraint constraint){
        SchemaCard schemaCard=getPlayerById(fsm.getUserPlayingId()).getSchema();
        try {
            schemaCard.putDie(placements.get(newIndex),selectedDie,constraint);
            getDraftPool().removeDie(oldIndex);
            return true;
        } catch (IllegalDieException e) {
            return false;
        }
    }

    /**
     * Returna the Player reference binded to a certain User
     * @param user the User to find the relative Player
     * @return the Player's reference
     */
    public Player getPlayer(User user) {
        for(Player p : players){
            if (p.matchesUser(user)){
                return p;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Returns the Player reference associated with a certain ID
     * @param playerId the Player's ID
     * @return the Player's reference
     */
    public Player getPlayerById(int playerId) {
        for(Player p : players){
            if (p.getGameId()==playerId){
                return p;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Returns the ToolCard associated with a certain index (0 to 2)
     * @param index the index of the ToolCard
     * @return the requested ToolCard's referece
     */
    public ToolCard getToolCard(int index) {
        assert (index>=0 && index<NUM_TOOLS);
        return toolCards[index];
    }

    /**
     * Returns the PubObjectiveCard associated with a certain index (0 to 2)
     * @param index the index of the PubObjectiveCard
     * @return the requested PubObjectiveCard's referece
     */
    public PubObjectiveCard getPublicObjective(int index) {
        assert (index>=0 && index<NUM_OBJECTIVES);
        return publicObjectives[index];
    }

    /**
     * Returns the DraftPool's reference
     * @return the DraftPool's reference
     */
    public DraftPool getDraftPool(){
        return this.draftPool;
    }

    /**
     * Returns the FSM reference
     * @return the FSM reference
     */
    public ServerFSM getFSM(){
        return fsm;
    }

}
