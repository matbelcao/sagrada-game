package it.polimi.ingsw.server.model;


import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.enums.ServerState;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.iterators.RoundIterator;
import org.jetbrains.annotations.NotNull;

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
    private boolean diePlaced;
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
                if(userPlaying.getStatus().equals(UserStatus.PLAYING) && userPlaying.getGame().equals(this)){
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

    private void roundFlow() {
        status=fsm.newTurn(round.isFirstTurn());
        diePlaced=false;

        exit();
        System.out.println(status+"  "+fsm.getPlaceFrom());
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
        if(board.getPlayerById(playerId).getSchema()==null){ throw new IllegalActionException(); }
        if(playerId>=0 && playerId<users.size()){
            return board.getPlayerById(playerId).getSchema();
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
        return board.getPlayer(user).getSchema();
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
        System.out.println("dice_list: "+status);
        if(!(status.equals(ServerState.MAIN)||status.equals(ServerState.GET_DICE_LIST))){throw new IllegalActionException();}
        if(!fsm.isToolActive() && diePlaced){throw new IllegalActionException();}

        Color constraint = Color.NONE;

        if(fsm.isToolActive()){
            constraint=selectedTool.getColorConstraint();
            if(selectedTool.isInternalSchemaPlacement()){
                diceList=selectedTool.internalIndexedSchemaDiceList();
                enableToolList=true;
            }else if(enableToolList && diceList.isEmpty()){ //skip if it's not required to select a die (ALL option)
                while(!status.equals(ServerState.TOOL_CAN_CONTINUE)){
                    status=fsm.nextState(Commands.NONE);
                }
                return diceList;
            }
        }

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
        System.out.println("select: "+status+" "+diceList.size()+" "+dieIndex);
        if(!status.equals(ServerState.SELECT) || diceList.size()<=dieIndex){throw new IllegalActionException();}

        Color constraint = Color.NONE;

        if(fsm.isToolActive()) {
            //toolcard enabled
            constraint = selectedTool.getColorConstraint();
            if(selectedTool.isInternalSchemaPlacement()){
                selectedDie=selectedTool.internalSelectDie(dieIndex);
            }else if (selectedCommand.equals(Commands.INCREASE_DECREASE) || selectedCommand.equals(Commands.SET_SHADE)) {
                selectedDie.setColor(diceList.get(dieIndex).getContent().getColor().toString());
                selectedDie.setShade(diceList.get(dieIndex).getContent().getShade().toInt());
            } else{
                selectedDie = board.selectDie(userPlayingId, fsm.getPlaceFrom(), dieIndex, constraint);
                oldIndex = board.getDiePosition(userPlayingId, fsm.getPlaceFrom(), selectedDie);
                selectedTool.selectDie(selectedDie, oldIndex);
            }
            commandsList = selectedTool.getActions();
        }else {
            //Toolcard disabled
            selectedDie = board.selectDie(userPlayingId, fsm.getPlaceFrom(), dieIndex, constraint);
            oldIndex = board.getDiePosition(userPlayingId, fsm.getPlaceFrom(), selectedDie);
            commandsList=new ArrayList<>();
            commandsList.add(Commands.PLACE_DIE);
        }
        System.out.println("selected-->"+selectedDie.toString());

        status=fsm.nextState(selectedCommand);
        enableToolList =false;
        return commandsList;
    }


    public boolean choose(User user,int index) throws IllegalActionException {
        Boolean response;

        switch(status){
            case INIT:
                System.out.println("CHOOSE_SCHEMA: "+status+" "+index);
                return chooseSchemaCard(user,index);
            case CHOOSE_OPTION:
                if(!user.equals(userPlaying)){throw new IllegalActionException();}
                System.out.println("CHOOSE_OPTIONS: "+status+" "+index);
                response=chooseOption(index);
                break;
            case CHOOSE_PLACEMENT:
                if(!user.equals(userPlaying)){throw new IllegalActionException();}
                System.out.println("CHOOSE_PLACEMENTS: "+placements.size()+" "+selectedCommand+" "+selectedDie);
                response=choosePlacement(index);
                break;
            default:
                throw new IllegalActionException();
        }

        if(response){
            status=fsm.nextState(selectedCommand);
            System.out.println("CHOOSE_end: "+status+" "+index);
        }
        System.out.println(selectedDie.getShade().toString());
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
                    return selectedTool.swapDie();
                case REROLL:
                    diceList=selectedTool.rerollDie();
                    enableToolList =true;
                    break;
                case FLIP:
                    diceList=selectedTool.flipDie();
                    break;
                case SET_SHADE:
                    selectedDie=board.getDraftPool().putInBagAndExtract(selectedDie);
                    diceList=selectedTool.chooseShade();
                    enableToolList =true;
                    fsm.setPlaceFrom(Place.DICEBAG);
                    break;
                case SET_COLOR:
                    selectedTool.setColor();
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
        boolean response;
        if(placements.size()<=index || !selectedCommand.equals(Commands.PLACE_DIE) || selectedDie==null){return false;}
        if(fsm.isToolActive()){
            if(selectedTool.isInternalSchemaPlacement()){
                response=selectedTool.internalDiePlacement(index);
            }else{
                response=board.schemaPlacement(userPlayingId,index,oldIndex,selectedDie);
                diePlaced=true;
            }
        }else{
            response=board.schemaPlacement(userPlayingId,index,oldIndex,selectedDie);
            diePlaced=true;
        }
        return response;
    }

    public List<Integer> getPlacements(User user) throws IllegalActionException {
        System.out.println("GET_PLACEMENTS: "+status);
        SchemaCard schema;

        if(status.equals(ServerState.GET_PLACEMENTS)) {
            IgnoredConstraint constraint=IgnoredConstraint.NONE;
            if(fsm.isToolActive()) {
                if(selectedTool.isInternalSchemaPlacement()){
                    placements=selectedTool.internalListPlacements();
                }else{
                    schema=board.getPlayer(user).getSchema();// TODO: 14/06/2018  inserire in toolcard
                    constraint = selectedTool.getIgnoredConstraint();
                    placements = board.listSchemaPlacements(schema, selectedDie,constraint);
                }

            }else{
                constraint=IgnoredConstraint.NONE;// TODO: 14/06/2018
                schema=board.getPlayer(user).getSchema();
                placements = board.listSchemaPlacements(schema, selectedDie,constraint);
            }


            status=fsm.nextState(selectedCommand);
            return placements;
        }
        throw new IllegalActionException();
    }

    public boolean activeTool(int index) throws IllegalActionException {
        System.out.println("TOOL_ENABLE: "+status);
        if(!status.equals(ServerState.MAIN)){ throw new IllegalActionException(); }
        if(index<0||index>2){return false;}


        Player player=board.getPlayerById(userPlayingId);
        Turn turn = round.isFirstTurn()?Turn.FIRST_TURN:Turn.SECOND_TURN;
        int roundNumber = round.getRoundNumber();

        Boolean toolEnabled=board.getToolCard(index).enableToolCard(player,roundNumber,turn,diePlaced,player.getSchema());
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
            }
        }else{
            discard();
            status=fsm.exit();
        }
        return toolEnabled;
    }

    public boolean toolStatus(User user) throws IllegalActionException {
        System.out.println("TOOL_STATUS: "+status);
        if(!status.equals(ServerState.TOOL_CAN_CONTINUE)){throw new IllegalActionException();}

        if(!selectedTool.toolCanContinue(board.getPlayer(user))){
            List<Integer> oldIndexes=selectedTool.getOldIndexes();
            board.removeOldDice(user,selectedTool.getPlaceFrom(),oldIndexes);
            exit();
        }else{
            status=fsm.nextState(selectedCommand);
        }
        return fsm.isToolActive();
    }


    /**
     * Allows the User to discard a multiple-message command (for COMPLEX ACTIONS like putDie(), ToolCard usages, ecc)
     */
    public void discard(){
        status=fsm.discard();
        selectedDie=null;
    }

    public void exit(){
        status=fsm.exit();
        enableToolList =false;
        selectedTool=null;
        selectedDie=null;
        diceList=new ArrayList<>();
        selectedCommand=Commands.NONE;
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
    @NotNull
    @Override
    public Iterator iterator() {
        return new RoundIterator(users);
    }

}