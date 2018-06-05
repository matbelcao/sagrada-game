package it.polimi.ingsw.server.model;


import it.polimi.ingsw.common.enums.GameStatus;
import it.polimi.ingsw.common.enums.ModifyDie;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.connection.User;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
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
    private User userPlaying;
    private Timer timer;
    private Die selectedDie;
    private boolean placedDie;
    private int selectedTool;
    private GameStatus gameStatus;


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
        this.gameStatus=GameStatus.INITIALIZING;
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
        this.gameStatus=GameStatus.INITIALIZING;
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
                    e.printStackTrace();
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
            board.getDraftPool().draftDice(users.size());

            //Notify to all the users the starting of the round
            for(User u:users){
                u.getServerConn().notifyRoundEvent("start",round.getRoundNumber());
            }

            while(round.hasNext()){
                gameStatus=GameStatus.TURN_RUN;
                selectedDie=null;
                placedDie=false;
                endLock=false;
                userPlaying = round.next();
                try {
                    discard();
                } catch (IllegalActionException e) { }

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

            //Notify to all the users the ending of the round
            for(User u:users){
                u.getServerConn().notifyRoundEvent("end",round.getRoundNumber());
            }
            board.getDraftPool().clearDraftPool(round.getRoundNumber());
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
     * @param override true to not DISCARD the complex action (and reset the RoundStatus class)
     * @return the card requested
     */
    public SchemaCard getUserSchemaCard(int playerId,boolean override) throws IllegalActionException {
        if(board.getPlayerById(playerId).getSchema()==null){ throw new IllegalActionException(); }
        if(selectedTool!=-1){
            if(!board.getToolCard(selectedTool).stageFrom(Place.SCHEMA)){
                if(!board.getToolCard(selectedTool).stageTo(Place.SCHEMA)){throw new IllegalActionException();}
            }
        }
        if(!override){
            gameStatus=GameStatus.REQUESTED_SCHEMA_CARD;
        }
        if(playerId>=0 && playerId<users.size()){
            return board.getPlayerById(playerId).getSchema();
        }
        return null;
    }

    /**
     * Responds to the request by sending the user's schema card
     * @param user the user who made the request
     * @param override true to not DISCARD the complex action (and reset the RoundStatus class)
     * @return the card requested
     */
    public SchemaCard getUserSchemaCard(User user, boolean override) throws IllegalActionException {
        if(board.getPlayer(user).getSchema()==null){ throw new IllegalActionException(); }
        if(selectedTool!=-1){
            if(!board.getToolCard(selectedTool).stageFrom(Place.SCHEMA)){
                if(!board.getToolCard(selectedTool).stageTo(Place.SCHEMA)){throw new IllegalActionException();}
            }
        }
        if(!override){
            gameStatus=GameStatus.REQUESTED_SCHEMA_CARD;
        }
        return board.getPlayer(user).getSchema();
    }





    /**
     * Responds to the request by sending the draftpool's content to the user of the match
     * @param override true to not DISCARD the complex action (and reset the RoundStatus class)
     * @return the list of die in the draftpool
     */
    public List<Die> getDraftedDice(boolean override) throws IllegalActionException {
        if(gameStatus==GameStatus.INITIALIZING){ throw new IllegalActionException(); }
        if(selectedTool!=-1){
            if(!board.getToolCard(selectedTool).stageFrom(Place.DRAFTPOOL)){
                if(!board.getToolCard(selectedTool).stageTo(Place.DRAFTPOOL)){throw new IllegalActionException();}
            }
        }
        if(!override){
            gameStatus=GameStatus.REQUESTED_DRAFT_POOL;
        }
        return board.getDraftPool().getDraftedDice();
    }



    /**
     * Responds to the request by sending the roundracks's content to the user of the match
     * @param override true to not DISCARD the complex action (and reset the RoundStatus class)
     * @return the list of die in the roundtrack
     */
    public List<List<Die>> getRoundTrackDice(boolean override) throws IllegalActionException {
        if(gameStatus==GameStatus.INITIALIZING){ throw new IllegalActionException(); }
        if(selectedTool!=-1){
            if(!board.getToolCard(selectedTool).stageFrom(Place.ROUNDTRACK)){
                if(!board.getToolCard(selectedTool).stageTo(Place.ROUNDTRACK)){throw new IllegalActionException();}
            }
        }
        if(!override){
            gameStatus=GameStatus.REQUESTED_ROUND_TRACK;
        }
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
     * Sets the chosen schema card to the user's relative player instance, if all the player have choose a schema card the timer will be stopped
     * @param user the user to set the card
     * @param schemaIndex the index of the schema card (for each player (0 to 3)
     * @return true iff the operation was successful
     */
    public boolean chooseSchemaCard(User user,int schemaIndex){
        boolean response;
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

    /**
     * Selects the die from a draftpool/user's schema card/roundtrack a returns the schema card's possible placements
     * @param user the user who made the request
     * @param index the index of the die to select
     * @return the list of possible placements in the user's schema card
     */
    // To continue........deve restituire List<Die>...solo per piazzamenti nella schema (o alcune toolcard)
    public List<Integer> selectDie(User user, int index) throws IllegalActionException {
        List<Integer> placements= new ArrayList<>();
        Die die;
        int tempIndex=0;

        if(gameStatus==GameStatus.INITIALIZING || gameStatus==GameStatus.TURN_RUN){ throw new IllegalActionException(); }
        if(gameStatus.equals(GameStatus.REQUESTED_SCHEMA_CARD)){
            FullCellIterator diceIterator=(FullCellIterator)board.getPlayer(user).getSchema().iterator();
            while(diceIterator.hasNext()){
                die=diceIterator.next().getDie();
                if(tempIndex==index){
                    selectedDie=die;
                }
                tempIndex++;
            }
        }
        if(gameStatus.equals(GameStatus.REQUESTED_DRAFT_POOL)){
            selectedDie=board.getDraftPool().getDraftedDice().get(index);
        }
        if(gameStatus.equals(GameStatus.REQUESTED_ROUND_TRACK)) {
            List<List<Die>> trackList = getRoundTrackDice(true);
            List<Die> dieList;
            int roundN = 0;

            while (tempIndex <= index) {
                dieList = trackList.get(roundN);
                for (Die d : dieList) {
                    if (tempIndex == index) {
                        //Routine selection
                        selectedDie=d;
                    }
                    tempIndex++;
                }
                roundN++;
            }
        }
        placements=board.listSchemaPlacements(user,selectedTool,selectedDie);
        return placements;
    }


    /**
     * Puts in the user's schemacard/draftpool/roundtrack the die if possible
     * @param index the index of the die to be placed
     * @return true iff the operation was successful
     */
    public boolean putDie(User user,int index) throws IllegalActionException {
        Die die;
        int realIndex=0;

        if(selectedDie!=null && !placedDie){
            if(gameStatus.equals(GameStatus.REQUESTED_DRAFT_POOL)){
                SchemaCard schemaCard=board.getPlayer(user).getSchema();
                realIndex=schemaCard.listPossiblePlacements(selectedDie).get(index);
                try {
                    schemaCard.putDie(realIndex,selectedDie);
                    board.getDraftPool().chooseDie(realIndex);
                    discard();
                    placedDie=true;
                    return true;
                } catch (IllegalDieException e) {
                    return false;
                }
            }
        }
        return false;
    }

    //selects and enables the tool
    public boolean chooseTool(User user,int index) throws IllegalActionException {
        Boolean toolEnabled;
        if(gameStatus==GameStatus.INITIALIZING){ throw new IllegalActionException(); }
        if(index<0||index>2){return false;}
        toolEnabled=board.getToolCard(index).enableToolCard(board.getPlayer(user),round.isFirstTurn()?0:1);
        if(toolEnabled){
            selectedTool=index;
        }else{
            discard();
        }
        return toolEnabled;
    }

    public boolean chooseToolDie(int index,String action) throws IllegalActionException {
        boolean result;
        Die die=findChoosedDie(index);

        if(gameStatus==GameStatus.INITIALIZING || gameStatus==GameStatus.TURN_RUN || selectedTool==-1){ throw new IllegalActionException(); }

        result=board.getToolCard(selectedTool).modifyDie1(die,ModifyDie.toModifyDie(action));
        if(!result){
            result=board.getToolCard(selectedTool).swapDie(die);
        }
        return result;
    }

    public boolean chooseToolDie(int index) throws IllegalActionException {
        boolean result;
        Die die=findChoosedDie(index);

        if(gameStatus==GameStatus.INITIALIZING || gameStatus==GameStatus.TURN_RUN || selectedTool==-1){ throw new IllegalActionException(); }

        result=board.getToolCard(selectedTool).selectDie1(die);
        return result;
    }

    public boolean chooseToolDieFace(int shade) throws IllegalActionException {
        boolean result;
        if(gameStatus==GameStatus.INITIALIZING || gameStatus==GameStatus.TURN_RUN || selectedTool==-1){ throw new IllegalActionException(); }
        if(shade<0||shade>6){ return false;}

        result=board.getToolCard(selectedTool).setShade(shade);
        return result;
    }

    private Die findChoosedDie(int index)  {
        int tempIndex=0;

        if(gameStatus.equals(GameStatus.REQUESTED_DRAFT_POOL)){
            return board.getDraftPool().getDraftedDice().get(index);
        }else if(gameStatus.equals(GameStatus.REQUESTED_ROUND_TRACK)) {
            List<List<Die>> trackList = board.getDraftPool().getRoundTrack().getTrack();
            List<Die> dieList;
            int roundN = 0;

            while (tempIndex <= index) {
                dieList = trackList.get(roundN);
                for (Die d : dieList) {
                    if (tempIndex == index) {
                        return d;
                    }
                    tempIndex++;
                }
                roundN++;
            }
        }
        return null;
    }

    /**
     * Allows the User to discard a multiple-message command (for COMPLEX ACTIONS like putDie(), ToolCard usages, ecc)
     */
    public void discard() throws IllegalActionException {
        if(gameStatus==GameStatus.INITIALIZING){ throw new IllegalActionException(); }
        selectedDie=null;
        selectedTool=-1;
        gameStatus=GameStatus.TURN_RUN;
    }


    /**
     * Notify to the active users that an user has been reconnected to the game
     * @param user the user to notify
     */
    public void reconnectUser(User user){
        for(User u : users){
            if(u.getStatus()==UserStatus.PLAYING){
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
            if(u.getStatus()==UserStatus.PLAYING){
                u.getServerConn().notifyStatusUpdate("disconnect",users.indexOf(user));
            }
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
            if(u.getStatus()==UserStatus.PLAYING){
                u.getServerConn().notifyStatusUpdate("quit",users.indexOf(user));
            }
        }
        users.remove(user);
    }

    public boolean gameStarted() {
        return gameStatus!=GameStatus.INITIALIZING;
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