package it.polimi.ingsw.server.model;


import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.enums.ServerState;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.iterators.RoundIterator;

import java.util.*;

/**
 * This class represents the controller of the game. It manages the rounds and the operations that the players make on the board
 */
public class Game extends Thread implements Iterable  {
    public static final int NUM_ROUND=10;

    private Board board;
    private boolean additionalSchemas; //to be used for additional schemas FA
    private List<User> users;
    private SchemaCard [] draftedSchemas;
    private RoundIterator round;
    private Boolean endLock;
    private final Object lockRun;
    private Timer timer;

    private ServerFSM fsm;
    private ServerState status;

    private User userPlaying;
    private int userPlayingId;

    private ToolCard selectedTool;


    private Die selectedDie;
    private int oldIndex;

    private Commands selectedCommand;
    private int numDiePlaced;
    private Boolean enableToolList;
    List<IndexedCellContent> diceList;
    List<Commands> commandsList;
    List<Integer> placements;


    /**
     * Constructs the class and sets the players list
     * @param users the players of the match
     * @param additionalSchemas true if additional are wanted by the user
     */
    public Game(List<User> users,boolean additionalSchemas){
        this.users= users;
        this.board=new Board(users,additionalSchemas);
        this.lockRun = new Object();
        this.draftedSchemas = board.draftSchemas();
        fsm=new ServerFSM();
        status=ServerState.INIT;
        this.selectedDie=null;
        for(User u : users){
            u.setStatus(UserStatus.PLAYING);
            u.setGame(this);
            u.getServerConn().notifyGameStart(users.size(), users.indexOf(u));
        }
    }

    /**
     * Constructs the class and sets the players list
     * @param users the players of the match
     */
    public Game(List<User> users){
        this.additionalSchemas=false;
        this.users= users;
        this.board=new Board(users,additionalSchemas);
        this.lockRun = new Object();
        draftedSchemas = board.draftSchemas();
        fsm=new ServerFSM();
        status=ServerState.INIT;
        this.selectedDie=null;
        for(User u : users){
            u.setStatus(UserStatus.PLAYING);
        }
    }

    /**
     * Assigns to the users who have not chosen any schema card, the first one that was proposed them before
     */
    private class DefaultSchemaAssignment extends TimerTask {
        @Override
        public void run(){
            Player player;
            for (User u:users){
                player=board.getPlayer(u);
                if(player.getSchema()==null){
                    player.setSchema(draftedSchemas[users.indexOf(u)*Board.NUM_PLAYER_SCHEMAS]);
                }
            }
            synchronized (lockRun) {
                endLock = true;
                lockRun.notifyAll();
            }
        }
    }

    /**
     * Allows the enabled user to perform the desired actions during his turn ( until it isn't executed )
     */
    private class PlayerTurn extends TimerTask {
        @Override
        public void run(){
            synchronized (lockRun) {
                endLock = true;
                lockRun.notifyAll();
            }
        }
    }

    /**
     * Stops the execution flow of Run() until the desired action occurs
     */
    private void stopFlow(){
        synchronized (lockRun) {
            while (!endLock) {
                try {
                    lockRun.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            lockRun.notifyAll();
        }
    }

    /**
     * Restart the execution flow of Run() because the desired action has occurred
     */
    public void startFlow(){
        synchronized (lockRun) {
            if(!endLock){
                endLock = true;
                timer.cancel();//DA SOSTITUIRE
                lockRun.notifyAll();
            }
        }
    }

    /**
     * This method provides the execution order of the game flow
     */
    @Override
    public void run(){
        endLock=false;
        round = (RoundIterator) this.iterator();

        timer = new Timer();
        timer.schedule(new DefaultSchemaAssignment(), MasterServer.getMasterServer().getTurnTime() * 1000);
        stopFlow();

        while (round.hasNextRound()){
            round.nextRound();
            enableToolList =false;
            board.getDraftPool().draftDice(users.size());

            //Notify to all the users the starting of the round
            notifyRoundStart();

            while(round.hasNext()){
                userPlaying = round.next();
                userPlayingId = board.getPlayer(userPlaying).getGameId();
                Player curPlayer= board.getPlayerById(userPlayingId);

                if(userPlaying.getStatus().equals(UserStatus.PLAYING) && userPlaying.getGame().equals(this) && !curPlayer.isSkippingTurn()){
                    roundFlow();
                }else if (getUsersActive()<=1){
                    //todo go to the last round
                    System.out.println("La partita sarebbe finita!");
                }
            }

            //Notify to all the users the ending of the round
            notifyRoundEnd();
            board.getDraftPool().clearDraftPool(round.getRoundNumber());
        }
        /*for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                u.getServerConn().notifyGameEnd(board.getPlayers);
            }
        }*/
    }

    private void notifyRoundStart() {
        for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                u.getServerConn().notifyRoundEvent("start", round.getRoundNumber());
            }
        }
    }

    private void notifyRoundEnd() {
        for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                u.getServerConn().notifyRoundEvent("end", round.getRoundNumber());
            }
        }
    }

    private void notifyBoardChanged(){
        for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                u.getServerConn().notifyBoardChanged();
            }
        }
    }

    private void roundFlow() {
        status=fsm.newTurn(round.isFirstTurn());
        numDiePlaced=0;

        exit(false);
        endLock=false;

        //Notify to all the users the starting of the turn
        for(User u:users){
            u.getServerConn().notifyTurnEvent("start",board.getPlayer(userPlaying).getGameId(),round.isFirstTurn()?0:1);
        }

        timer = new Timer();
        timer.schedule(new PlayerTurn(), MasterServer.getMasterServer().getTurnTime() * 1000);
        stopFlow();

        //Notify to all the users the ending of the turn
        for(User u:users){
            u.getServerConn().notifyTurnEvent("end",board.getPlayer(userPlaying).getGameId(),round.isFirstTurn()?0:1);
        }
    }

    public User getUserPlaying(){
        return userPlaying;
    }


    /**
     * ReUser spon){
     *     reuserPlaying;rn us
     * }ds to the request by sending one private objective card to the user of the match
     * @param user the user who made the request
     * @return the card requested
     */
    public PrivObjectiveCard getPrivCard(User user){
        return board.getPlayer(user).getPrivObjective();
    }

    /**
     * Responds to the request by sending three public objective cards to the user of the match
     * @return the list of cards requested
     */
    public List<PubObjectiveCard> getPubCards(){
        List<PubObjectiveCard> cards= new ArrayList<>();

        for (int i=0 ; i < Board.NUM_OBJECTIVES ; i++ ) {
            cards.add(board.getPublicObjective(i));
        }
        return cards;
    }

    /**
     * Responds to the request by sending three tool cards to the user of the match
     * @return the list of cards requested
     */
    public List<ToolCard> getToolCards(){
        List<ToolCard> cards=new ArrayList<>();

        for (int i = 0; i < Board.NUM_TOOLS; i++) {
            cards.add(board.getToolCard(i));
        }
        return cards;
    }

    /**
     * Responds to the request by sending four schema cards to the user of the match
     * @param user the user who made the request
     * @return the list of cards requested
     */
    public List<SchemaCard> getDraftedSchemaCards(User user) throws IllegalActionException {
        if(board.getPlayer(user).getSchema()!=null){ throw new IllegalActionException(); }
        List<SchemaCard> schemas=new ArrayList<>();
        for (int i=0 ; i < Board.NUM_PLAYER_SCHEMAS ; i++ ){
            schemas.add(draftedSchemas[(users.indexOf(user)* Board.NUM_PLAYER_SCHEMAS)+i]);
        }
        return schemas;
    }

    /**
     * Responds to the request by sending the player-specific schema card
     * @param playerId the id of the player's desired schema card
     * @return the card requested
     */
    public SchemaCard getUserSchemaCard(int playerId) throws IllegalActionException {
        if(playerId>=users.size() || playerId <0){ throw new IllegalActionException(); }
        if(board.getPlayerById(playerId).getSchema()==null){ throw new IllegalActionException(); }
        if(playerId>=0 && playerId<users.size()){
            if(fsm.isToolActive()){
                return selectedTool.getNewSchema();
            }else{
                return board.getPlayerById(playerId).getSchema();
            }
        }
        return null;
    }

    /**
     * Responds to the request by sending the user's schema card
     * @param user the user who made the request
     * @return the card requested
     */
    public SchemaCard getUserSchemaCard(User user) throws IllegalActionException {
        if(board.getPlayer(user).getSchema()==null){ throw new IllegalActionException(); }
        if(fsm.isToolActive()){
            return selectedTool.getNewSchema();
        }else {
            return board.getPlayer(user).getSchema();
        }
    }

    /**
     * Responds to the request by sending the draftpool's content to the user of the match
     * @return the list of die in the draftpool
     */
    public List<Die> getDraftedDice() throws IllegalActionException {
        if(status.equals(ServerState.INIT)){ throw new IllegalActionException(); }
        return board.getDraftPool().getDraftedDice();
    }

    /**
     * Responds to the request by sending the roundracks's content to the user of the match
     * @return the list of die in the roundtrack
     */
    public List<List<Die>> getRoundTrackDice() throws IllegalActionException {
        if(status.equals(ServerState.INIT)){ throw new IllegalActionException(); }
        return board.getDraftPool().getRoundTrack().getTrack();
    }

    /**
     * Responds to the request by sending the list of the match's players (and their id inside the Player class)
     * @return the list of players in the current match
     */
    public List<Player> getPlayers(){
        List<Player> players= new ArrayList<>();
        for (User u:users){
            players.add(board.getPlayer(u));
        }
        return players;
    }

    /**
     * Responds by sending the number of favor tokens owned by the user
     * @param playerId the id of the user to get the favor tokens
     */
    public int getFavorTokens(int playerId){
        Player player=board.getPlayerById(playerId);
        if(player.getSchema()!=null){
            return player.getFavorTokens();
        }
        return 0;
    }


    /**
     * Returns to the User who made the request an indexed List of dice contained in a specific board position.
     * The selection of the interested area is automated by the FSM and the game logic
     * @return
     * @throws IllegalActionException
     */
    public List<IndexedCellContent> getDiceList() throws IllegalActionException {
        if(!(status.equals(ServerState.MAIN)||status.equals(ServerState.GET_DICE_LIST))){throw new IllegalActionException();}
        if(!fsm.isToolActive() && numDiePlaced>=1){throw new IllegalActionException();}

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
        //System.out.println("GET_DICE_LIST: "+status+" "+fsm.getPlaceFrom()+" "+enableToolList);

        if(!enableToolList){
            diceList=board.indexedDiceList(userPlayingId,fsm.getPlaceFrom(),constraint);
        }

        if(status.equals(ServerState.MAIN)){
            status=fsm.nextState(selectedCommand);
        }
        status=fsm.nextState(selectedCommand);
        return diceList;
    }

    public List<Commands> selectDie(int dieIndex) throws IllegalActionException {
        //System.out.println("SELECT_DIE: "+status+" "+diceList.size()+" "+dieIndex);
        if(!status.equals(ServerState.SELECT) || diceList.size()<=dieIndex){throw new IllegalActionException();}

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
                selectedDie = board.selectDie(userPlayingId, fsm.getPlaceFrom(), dieIndex, constraint);
                oldIndex = board.getDiePosition(userPlayingId, fsm.getPlaceFrom(), selectedDie);
                selectedTool.selectDie(selectedDie);
            }
            commandsList = selectedTool.getActions();
        }else {
            //Toolcard disabled
            selectedDie = board.selectDie(userPlayingId, fsm.getPlaceFrom(), dieIndex, constraint);
            oldIndex = board.getDiePosition(userPlayingId, fsm.getPlaceFrom(), selectedDie);
            commandsList=new ArrayList<>();
            commandsList.add(Commands.PLACE_DIE);
        }
        //System.out.println("selected-->"+selectedDie.toString());

        status=fsm.nextState(selectedCommand);
        enableToolList =false;
        return commandsList;
    }


    public boolean choose(User user,int index) throws IllegalActionException {
        Boolean response;

        switch(status){
            case INIT:
                //System.out.println("CHOOSE_SCHEMA: "+status+" "+index);
                return chooseSchemaCard(user,index);
            case CHOOSE_OPTION:
                if(!user.equals(userPlaying)){throw new IllegalActionException();}
                //System.out.println("CHOOSE_OPTIONS: "+status+" "+index);
                response=chooseOption(index);
                break;
            case CHOOSE_PLACEMENT:
                if(!user.equals(userPlaying)){throw new IllegalActionException();}
                //System.out.println("CHOOSE_PLACEMENTS: "+placements.size()+" "+selectedCommand+" "+selectedDie);
                response=choosePlacement(index);
                break;
            default:
                throw new IllegalActionException();
        }

        if(response){
            status=fsm.nextState(selectedCommand);
        }
        return response;
    }

    /**
     * Sets the chosen schema card to the user's relative player instance, if all the player have choose a schema card the timer will be stopped
     * @param user the user to set the card
     * @param schemaIndex the index of the schema card (for each player (0 to 3)
     * @return true iff the operation was successful
     */
    private boolean chooseSchemaCard(User user,int schemaIndex){
        boolean response;
        if(schemaIndex<0 || schemaIndex >=4){return false;}
        response=board.getPlayer(user).setSchema(draftedSchemas[(users.indexOf(user)*Board.NUM_PLAYER_SCHEMAS)+schemaIndex]);
        if(!response){return false;}
        for (User u: users){
            if(board.getPlayer(u).getSchema()==null){
                return true;
            }
        }
        startFlow();
        return true;
    }

    public boolean chooseOption(int index){
        if(commandsList.size()<=index){return false;}
        selectedCommand=commandsList.get(index);
        if(fsm.isToolActive()){
            switch (selectedCommand){
                case INCREASE_DECREASE:
                    diceList=selectedTool.shadeIncreaseDecrease(selectedDie);
                    enableToolList =true;
                    break;
                case SWAP:
                    fsm.setPlaceFrom(selectedTool.getPlaceTo());
                    return selectedTool.swapDie();
                case REROLL:
                    diceList=selectedTool.rerollDie();
                    enableToolList =true;
                    break;
                case FLIP:
                    diceList=selectedTool.flipDie();
                    break;
                case SET_SHADE:
                    fsm.setPlaceFrom(Place.DICEBAG);
                    selectedDie=board.getDraftPool().putInBagAndExtract(selectedDie);
                    diceList=selectedTool.chooseShade();
                    enableToolList =true;
                    break;
                case SET_COLOR:
                    selectedTool.setColor();
                    enableToolList =true;
                    fsm.setPlaceFrom(selectedTool.getPlaceFrom());
                    return true;
                case PLACE_DIE:
                    return true;
                case NONE:
                    return true;
                default:
                    return false;
            }
            return diceList != null;
        }else{
            return true;
        }
    }

    private boolean choosePlacement(int index){
        if(placements.size()<=index || !selectedCommand.equals(Commands.PLACE_DIE) || selectedDie==null){return false;}
        boolean response;
        IgnoredConstraint constraint;

        if(fsm.isToolActive()){
            if(selectedTool.isInternalSchemaPlacement()){
                response=selectedTool.internalDiePlacement(index);
            }else{
                constraint = selectedTool.getIgnoredConstraint();
                response=board.schemaPlacement(userPlayingId,index,oldIndex,selectedDie,constraint);
                numDiePlaced++;
            }
        }else{
            constraint=IgnoredConstraint.NONE;
            response=board.schemaPlacement(userPlayingId,index,oldIndex,selectedDie,constraint);
            numDiePlaced++;
        }

        if(response){
            notifyBoardChanged();
        }
        return response;
    }

    public List<Integer> getPlacements(User user) throws IllegalActionException {
        //System.out.println("GET_PLACEMENTS: "+status);
        IgnoredConstraint constraint;

        if(status.equals(ServerState.GET_PLACEMENTS)) {
            if(fsm.isToolActive()) {
                if(selectedTool.isInternalSchemaPlacement()){
                    placements=selectedTool.internalListPlacements();
                }else{
                    constraint = selectedTool.getIgnoredConstraint();
                    placements = board.listSchemaPlacements(userPlayingId, selectedDie,constraint);
                }

            }else{
                constraint=IgnoredConstraint.NONE;
                placements = board.listSchemaPlacements(userPlayingId, selectedDie,constraint);
            }


            status=fsm.nextState(selectedCommand);
            return placements;
        }
        throw new IllegalActionException();
    }

    public boolean activeTool(int index) throws IllegalActionException {
        //System.out.println("TOOL_ENABLE: "+status);
        if(!status.equals(ServerState.MAIN)){ throw new IllegalActionException(); }
        if(index<0||index>2){return false;}


        Player player=board.getPlayerById(userPlayingId);
        Turn turn = round.isFirstTurn()?Turn.FIRST_TURN:Turn.SECOND_TURN;
        int roundNumber = round.getRoundNumber();

        Boolean toolEnabled=board.getToolCard(index).enableToolCard(player,roundNumber,turn,numDiePlaced,player.getSchema());
        if(toolEnabled){
            selectedTool=board.getToolCard(index);
            status=fsm.newToolUsage(selectedTool);
            if(selectedTool.isRerollAllDiceCard()){
                List<Die> dielist;
                switch (selectedTool.getPlaceFrom()){
                    case DRAFTPOOL:
                        dielist=board.getDraftPool().getDraftedDice();
                        break;
                    case ROUNDTRACK:
                        dielist=board.getDraftPool().getRoundTrack().getTrackList();
                        break;
                    default:
                        throw new IllegalActionException();
                }
                selectedTool.rerollAll(dielist);
                diceList=new ArrayList<>();
                enableToolList =true;
            }else if(selectedTool.isSetColorFromRountrackCard()){
                    fsm.setPlaceFrom(Place.ROUNDTRACK);
                    enableToolList=false;
            }
            notifyBoardChanged();
        }else{
            exit(false);
        }
        return toolEnabled;
    }

    public boolean toolStatus(User user) throws IllegalActionException {
        //System.out.println("TOOL_STATUS: "+status);
        if(!status.equals(ServerState.TOOL_CAN_CONTINUE)){throw new IllegalActionException();}

        if(!selectedTool.toolCanContinue(board.getPlayer(user))){
            List<Integer> oldIndexes=selectedTool.getOldIndexes();
            board.removeOldDice(user,selectedTool.getPlaceFrom(),oldIndexes);
            exit(false);
        }else{
            status=fsm.nextState(selectedCommand);
        }
        notifyBoardChanged();
        return fsm.isToolActive();
    }


    /**
     * Allows the User to discard a multiple-message command (for COMPLEX ACTIONS like putDie(), ToolCard usages, ecc)
     */
    public void discard(){
        if(!status.equals(ServerState.CHOOSE_PLACEMENT)){return;}
        if(fsm.isToolActive()){
            selectedTool.toolDiscard();
        }
        status=fsm.fsmDiscard();
        selectedDie=null;
    }

    public void exit(Boolean notifyBoardChanged){
        if(status.equals(ServerState.INIT)){return;}
        if(fsm.isToolActive()){
            selectedTool.toolExit(board.getPlayerById(userPlayingId));
        }
        status=fsm.fsmExit();
        enableToolList =false;
        selectedTool=null;
        selectedDie=null;
        diceList=new ArrayList<>();
        selectedCommand=Commands.NONE;
        if(notifyBoardChanged){
            notifyBoardChanged();
        }
    }


    public boolean canUserReconnect(User user){
        if(users.contains(user)){
            return !board.getPlayer(user).hasQuitted();
        }
        return false;
    }

    /**
     * Notify to the active users that an user has been reconnected to the game
     * @param user the user to notify
     */
    public void reconnectUser(User user){
        for(User u : users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)){
                u.getServerConn().notifyStatusUpdate("reconnect",users.indexOf(user));
            }
        }
        user.setStatus(UserStatus.PLAYING);
    }

    /**
     * Notify to the active users that an user has lost the connection to the game
     * @param user the user to notify
     */
    public void disconnectUser(User user){
        user.setStatus(UserStatus.DISCONNECTED);
        for(User u : users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)){
                u.getServerConn().notifyStatusUpdate("disconnect",users.indexOf(user));
            }
        }
        if((userPlaying!=null && userPlaying.equals(user))||getUsersActive()<=1){
            startFlow();
        }
    }

    /**
     * Notify to the active users that an user has left the game
     * @param user the user to notify
     */
    public void quitUser(User user){
        user.setStatus(UserStatus.DISCONNECTED);
        // add control for number of players still in the game...
        for(User u : users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)){
                u.getServerConn().notifyStatusUpdate("quit",users.indexOf(user));
            }
        }
        board.getPlayer(user).quitMatch();
        if((userPlaying!=null && userPlaying.equals(user))||getUsersActive()<=1){
            startFlow();
        }
    }

    public int getUsersActive(){
        int num=0;
        for(User u : users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)){
                num++;
            }
        }
        return num;
    }

    public boolean gameStarted() {
        return !status.equals(ServerState.INIT);
    }

    /**
     * Instantiates a new board for the match
     */
    public void createBoard(){
        this.board= new Board(this.users, additionalSchemas);
    }

    /**
     * Returns the list of the users that are currently playing the game
     * @return the list of users
     */
    public List<User> getUsers(){
        return users;
    }

    /**
     * This method creates an iterator that implements the round's turns management system
     * @return an iterator on the players of this game
     */
    @Override
    public Iterator iterator() {
        return new RoundIterator(users);
    }

}