package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.model.*;
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
    private boolean additionalSchemas; //to be used to enable additional schemas FA
    private List<User> users;
    private SchemaCard[] draftedSchemas;
    private RoundIterator round;
    private Boolean endLock;
    private final Object lockRun;
    private Timer timer;

    private ServerFSM fsm;

    private User userPlaying;
    private int diceListSize;


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
        fsm=board.getFSM();
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
        fsm=board.getFSM();
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
        timer.schedule(new DefaultSchemaAssignment(), MasterServer.getMasterServer().getTurnTime() * (long)1000);
        stopFlow();

        fsm.nextState(Actions.NONE);
        while (round.hasNextRound() && getUsersActive()>1){
            round.nextRound();
            board.getDraftPool().draftDice(users.size());

            //Notify to all the users the starting of the round
            notifyRoundStart();

            while(round.hasNext() && getUsersActive()>1){
                userPlaying = round.next();
                Player curPlayer= board.getPlayer(userPlaying);

                if(userPlaying.getStatus().equals(UserStatus.PLAYING) && userPlaying.getGame().equals(this) && !curPlayer.isSkippingTurn()) {
                    turnFlow();
                }
            }

            //Notify to all the users the ending of the round
            notifyRoundEnd();
            board.getDraftPool().clearDraftPool(round.getRoundNumber());
        }

        notifyGameEnd();
    }

    /**
     * Notifies to the connected clients the ending of the current match
     */
    private void notifyGameEnd() {
        List<RankingEntry> ranking;

        ranking=board.gameRunningEnd();
        fsm.endGame();
        System.out.println(fsm.getCurState());

        for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                u.getServerConn().notifyGameEnd(ranking);
                board.getPlayer(u).quitMatch();
                System.out.println("End match: "+u.getUsername());
            }
        }
    }

    public boolean isGameEnded(){
        return fsm.getCurState().equals(ServerState.GAME_ENDED);
    }

    /**
     * Notifies to the connected clients the beginning of a new Round
     */
    private void notifyRoundStart() {
        for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                u.getServerConn().notifyRoundEvent(Event.ROUND_START, round.getRoundNumber());
            }
        }
    }

    /**
     * Notifies to the connected clients the ending od the Round
     */
    private void notifyRoundEnd() {
        for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                u.getServerConn().notifyRoundEvent(Event.ROUND_END, round.getRoundNumber());
            }
        }
    }

    /**
     * Notifies to the connected clients that the current player's action has changed some Board parameters.
     * This message will trigger the client's update requests.
     */
    private void notifyBoardChanged(User user){
        for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this) && !u.equals(user)) {
                u.getServerConn().notifyBoardChanged();
            }
        }
    }

    /**
     *It contains the code for the correct flow of the enabled player's turn. Inside it is instatitiated a timer
     * to limit the maximum time of each turn.
     */
    private void turnFlow() {
        fsm.newTurn(board.getPlayer(userPlaying).getGameId(),round.isFirstTurn());
        back(false);

        endLock=false;

        //Notify to all the users the starting of the turn
        for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                u.getServerConn().notifyTurnEvent(Event.TURN_START, board.getPlayer(userPlaying).getGameId(), round.isFirstTurn() ? 0 : 1);
            }
        }

        timer = new Timer();
        timer.schedule(new PlayerTurn(), MasterServer.getMasterServer().getTurnTime() * (long)1000);
        stopFlow();

        //Notify to all the users the ending of the turn
        for(User u:users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                u.getServerConn().notifyTurnEvent(Event.TURN_END, board.getPlayer(userPlaying).getGameId(), round.isFirstTurn() ? 0 : 1);
            }
        }
    }

    /**
     * Responds to the request by sending the user that is currently playing his turn
     * @return the user that is currently playing
     */
    public User getUserPlaying(){
        return userPlaying;
    }


    /**
     * Responds to the request by sending one private objective card to the user of the match
     * @param user the user who made the request
     * @return the card requested
     */
    public LightPrivObj getPrivCard(User user){
        return LightPrivObj.toLightPrivObj(board.getPlayer(user).getPrivObjective());
    }

    /**
     * Responds to the request by sending three public objective cards to the user of the match
     * @return the list of cards requested
     */
    public List<LightCard> getPubCards(){
        List<LightCard> lightPubs = new ArrayList<>();

        for (int i=0 ; i < Board.NUM_OBJECTIVES ; i++ ) {
            lightPubs.add(LightCard.toLightCard(board.getPublicObjective(i)));
        }
        return lightPubs;
    }

    /**
     * Responds to the request by sending three tool cards to the user of the match
     * @return the list of cards requested
     */
    public List<LightTool> getToolCards(){
        List<LightTool> lightTools = new ArrayList<>();

        for (int i = 0; i < Board.NUM_TOOLS; i++) {
            lightTools.add(LightTool.toLightTool(board.getToolCard(i)));
        }
        return lightTools;
    }

    /**
     * Responds to the request by sending four schema cards to the user of the match
     * @param user the user who made the request
     * @return the list of cards requested
     * @throws IllegalActionException if the schema card is still not instantited
     */
    public List<LightSchemaCard> getDraftedSchemaCards(User user) throws IllegalActionException {
        if(board.getPlayer(user).getSchema()!=null){ throw new IllegalActionException(); }
        List<LightSchemaCard>  lightSchemas=new ArrayList<>();
        for (int i=0 ; i < Board.NUM_PLAYER_SCHEMAS ; i++ ){
            lightSchemas.add(LightSchemaCard.toLightSchema(draftedSchemas[(users.indexOf(user)* Board.NUM_PLAYER_SCHEMAS)+i]));
        }
        return lightSchemas;
    }

    /**
     * Responds to the request by sending the player-specific schema card. If the toolcard execution is enabled, it will
     * be sent the temporary schema card used during the tool-specific execution flow.
     * @param playerId the id of the player's desired schema card
     * @return the card requested
     * @throws IllegalActionException if the request syntax is wrong, if the schema card is still not instantited, if
     * the index is bigger than the List of dice
     */
    public LightSchemaCard getUserSchemaCard(int playerId) throws IllegalActionException {
        if(playerId>=users.size() || playerId <0){ throw new IllegalActionException(); }
        if(board.getPlayerById(playerId).getSchema()==null){ throw new IllegalActionException(); }
        if(playerId>=0 && playerId<users.size()){
            SchemaCard schemaCard=board.getUserSchemaCard(playerId);
            return LightSchemaCard.toLightSchema(schemaCard);
        }
        throw new IllegalActionException();
    }

    /**
     * Responds to the request by sending the user's schema card. If the toolcard execution is enabled, it will be sent
     * the temporary schema card used during the tool-specific execution flow.
     * @param user the user who made the request
     * @return the card requested
     * @throws IllegalActionException if the schema card is still not instantited
     */
    public LightSchemaCard getUserSchemaCard(User user) throws IllegalActionException {
        Player player= board.getPlayer(user);
        if(board.getPlayer(user).getSchema()==null){ throw new IllegalActionException(); }
        return getUserSchemaCard(player.getGameId());
    }

    /**
     * Responds to the request by sending the draftpool's content to the user of the match
     * @return the list of die in the draftpool
     * @throws IllegalActionException if the request syntax is wrong, if the fsm state is not correct, if the index is
     * bigger than the List of dice
     */
    public List<LightDie> getDraftedDice() throws IllegalActionException {
        if(fsm.getCurState().equals(ServerState.INIT)){ throw new IllegalActionException(); }
        List<Die> draftPool=board.getDraftPool().getDraftedDice();
        List<LightDie> lightDraftPool=new ArrayList<>();

        for(Die d:draftPool) {
            lightDraftPool.add(LightDie.toLightDie(d));
        }
        return lightDraftPool;

    }

    /**
     * Responds to the request by sending the roundracks's content to the user of the match
     * @return the list of die in the roundtrack
     * @throws IllegalActionException if the request syntax is wrong, if the fsm state is not correct, if the index is
     * bigger than the List of dice
     */
    public List<List<LightDie>> getRoundTrackDice() throws IllegalActionException {
        if(fsm.getCurState().equals(ServerState.INIT)){ throw new IllegalActionException(); }

        List<List<Die>> trackList = board.getDraftPool().getRoundTrack().getTrack();
        List<Die> dieList;

        List<List<LightDie>> roundTrack=new ArrayList<>();
        List<LightDie> container;

        for(int i=0;i<trackList.size();i++){
            dieList=trackList.get(i);
            container = new ArrayList<>();
            for(Die d:dieList){
                container.add(LightDie.toLightDie(d));
            }
            roundTrack.add(i, container);
        }
        return roundTrack;
    }

    /**
     * Responds to the request by sending the list of the match's players (and their id inside the Player class)
     * @return the list of players in the current match
     */
    public List<LightPlayer> getPlayers(){
        List<Player> players= new ArrayList<>();
        List<LightPlayer> lightPlayers = new ArrayList<>();
        for (User u:users){
            Player player=board.getPlayer(u);
            lightPlayers.add(new LightPlayer(player.getUsername(),player.getGameId()));
        }
        return lightPlayers;
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
     * The selection of the interested area is automated by the FSM and the Board logic.
     * @return the indexed List of dice contained in a specific board position
     * @throws IllegalActionException if the request syntax is wrong or if the fsm state is not correct
     */
    public List<IndexedCellContent> getDiceList() throws IllegalActionException {
        if(!(fsm.getCurState().equals(ServerState.MAIN)||fsm.getCurState().equals(ServerState.GET_DICE_LIST))){
            throw new IllegalActionException();
        }
        if(!fsm.isToolActive() && fsm.isDiePlaced()){throw new IllegalActionException();}

        List<IndexedCellContent> diceList=board.getDiceList();
        diceListSize=diceList.size();

        return diceList;
    }

    /**
     * Allows the player to select a die from a previously sent list.
     * Returns to the User who made the request an indexed List of commands that can be executed on a certain selected die.
     * @param dieIndex the index of the previously indexed dice List sent to the client
     * @return the indexed List of commands that can be executed
     * @throws IllegalActionException if the request syntax is wrong, if the fsm state is not correct, if the index is
     * bigger than the List of dice
     */
    public List<Actions> selectDie(int dieIndex) throws IllegalActionException {
        if(!fsm.getCurState().equals(ServerState.SELECT) || diceListSize<=dieIndex){throw new IllegalActionException();}

        return board.selectDie(dieIndex);
    }


    /**
     * Returns to the User who made the request the affirmative or negative answer to an action of choice.
     * The choice may concern: the selection of a card schema, an action to be performed, a placement.
     * @param user the user who made the request
     * @param index the index of the previously indexed List sent to the client
     * @return the indexed List of commands that can be executed
     * @throws IllegalActionException if the request syntax is wrong, if the fsm state is not correct, if the index is
     * bigger than the List of options
     */
    public boolean choose(User user,int index) throws IllegalActionException {
        Boolean response;

        switch(fsm.getCurState()){
            case INIT:
                response=board.chooseSchemaCard(user,index);
                if(response) {
                    for (User u: users){
                        if(board.getPlayer(u).getSchema()==null){
                            return true;
                        }
                    }
                    startFlow();
                }
                return response;
            case CHOOSE_OPTION:
                if(!user.equals(userPlaying)){throw new IllegalActionException();}
                response=board.chooseOption(index);
                break;
            case CHOOSE_PLACEMENT:
                if(!user.equals(userPlaying)){throw new IllegalActionException();}
                response=board.choosePlacement(index);
                if(response){
                    notifyBoardChanged(user);
                }
                break;
            default:
                throw new IllegalActionException();
        }
        return response;
    }

    /**
     * Returns to the User who made the request the list of possible placements if the selected action is PLACE_DIE.
     * The composition of the list, according to the various Constraints/ToolCards enabled, is automated by the board logic.
     * @return the list of possible placements, ordered in increasing order
     * @throws IllegalActionException if the request syntax is wrong, if the fsm state is not correct
     */
    public List<Integer> getPlacements() throws IllegalActionException {
        if(!fsm.getCurState().equals(ServerState.GET_PLACEMENTS)) {throw new IllegalActionException();}

        return board.getPlacements();
    }

    /**
     * Returns to the User who made the request the affirmative or negative answer to attempting to enable the selected
     * tool card. If the response is affirmative, the method will trigger the client's update requests.
     * @param index the index of the previously indexed List of tool cards sent to the client (0 to 2)
     * @return true if the ToolCard is successfully enabled
     * @throws IllegalActionException if the request syntax is wrong, if the fsm state is not correct, if the index is
     * OutOfBound (0 to 2)
     */
    public boolean activeTool(int index) throws IllegalActionException {
        if(!fsm.getCurState().equals(ServerState.MAIN)){ throw new IllegalActionException(); }
        if(index<0||index>2){return false;}

        Turn turn = round.isFirstTurn()?Turn.FIRST_TURN:Turn.SECOND_TURN;
        int roundNumber = round.getRoundNumber();

        Boolean toolEnabled=board.activeTool(index,turn,roundNumber);

        if(toolEnabled){
            notifyBoardChanged(userPlaying);
        }
        return toolEnabled;
    }

    /**
     * Returns to the user who made the request the ToolCard status.
     * A negative answer indicates that the execution of the action flow of the toolcard has ended.
     * @return true if the execution flow is not ended, false otherwise
     * @throws IllegalActionException if the request syntax is wrong or if the fsm state is not correct
     */
    public boolean toolStatus() throws IllegalActionException {
        if(!fsm.getCurState().equals(ServerState.TOOL_CAN_CONTINUE)){throw new IllegalActionException();}

        boolean response = board.toolStatus();

        notifyBoardChanged(userPlaying);

        return response;
    }


    /**
     * Allows the User to not perform a placement (with the current selected die) and select a new one without interrupting
     * the execution of a multiple-message command.
     */
    public void discard(){
        if(!fsm.getCurState().equals(ServerState.CHOOSE_PLACEMENT)){return;}
        board.discard();
    }

    /**
     * Allows the User to interrupt a multiple-message command (for COMPLEX ACTIONS like die placements, ToolCard usages, ecc)
     * @param notifyBoardChanged if true, this flag will trigger the client's update requests.
     */
    public void back(Boolean notifyBoardChanged){
        if(fsm.getCurState().equals(ServerState.INIT)){return;}

        board.exit();

        if(notifyBoardChanged){
            for(User u:users){
                if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)) {
                    u.getServerConn().notifyBoardChanged();
                }
            }
        }
    }

    /**
     * Allows an user to reconnect the game if previously the connection was interrupted by network problems.
     * @param user the user who wants to reconnect
     * @return true if the request is accepted, false otherwise
     */
    public boolean canUserReconnect(User user){
        if(users.contains(user)){
            return !board.getPlayer(user).hasQuitted();
        }
        return false;
    }

    /**
     * Notifies to the active players that an user has been reconnected to the game
     * @param user the user who has reconnected
     */
    public void reconnectUser(User user){
        for(User u : users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)){
                u.getServerConn().notifyStatusUpdate(Event.RECONNECT,users.indexOf(user));
            }
        }
        user.setStatus(UserStatus.PLAYING);
    }

    /**
     * Notifies to the active playerss that an user has lost the connection to the game
     * @param user the user whose connection has been lost
     */
    public void disconnectUser(User user){
        user.setStatus(UserStatus.DISCONNECTED);
        for(User u : users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)){
                u.getServerConn().notifyStatusUpdate(Event.DISCONNECT,users.indexOf(user));
            }
        }
        if((userPlaying!=null && userPlaying.equals(user))||getUsersActive()<=1){
            startFlow();
        }
    }

    /**
     * Notifies to the active players that an user has left the game
     * @param user the user who has quitted the match
     */
    public void quitUser(User user){
        user.setStatus(UserStatus.DISCONNECTED);
        // add control for number of players still in the game...
        for(User u : users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)){
                u.getServerConn().notifyStatusUpdate(Event.QUIT,users.indexOf(user));
            }
        }
        board.getPlayer(user).quitMatch();
        if((userPlaying!=null && userPlaying.equals(user))||(getUsersActive()<=1)){
            startFlow();
        }
    }

    /**
     * Returns the numbers users that are connected and are currently playing the game
     * @return the number of users connected
     */
    public int getUsersActive(){
        int num=0;
        for(User u : users){
            if(u.getStatus().equals(UserStatus.PLAYING) && u.getGame().equals(this)){
                num++;
            }
        }
        return num;
    }

    /**
     * Returns true if the game has started
     * @return if the game has started
     */
    public boolean gameStarted() {
        return !fsm.getCurState().equals(ServerState.INIT);
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