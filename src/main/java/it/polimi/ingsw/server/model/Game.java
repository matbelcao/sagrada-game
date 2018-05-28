package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.connection.User;
import it.polimi.ingsw.common.enums.UserStatus;
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
    private ArrayList<User> users;
    private SchemaCard [] draftedSchemas;
    private RoundIterator round;
    private Boolean endLock;
    private final Object lockRun;
    private User userPlaying;
    private Timer timer;
    RoundStatus roundStatus;

    /**
     * Constructs the class and sets the players list
     * @param users the players of the match
     * @param additionalSchemas true if additional are wanted by the user
     */
    public Game(List<User> users,boolean additionalSchemas){
        this.users= (ArrayList<User>) users;
        for(User u : users){
            u.setStatus(UserStatus.PLAYING);
            u.setGame(this);
            u.getServerConn().notifyGameStart(users.size(), users.indexOf(u));
        }
        this.board=new Board(users,additionalSchemas);
        this.lockRun = new Object();
        this.draftedSchemas = board.draftSchemas();
        this.roundStatus=new RoundStatus();
    }

    /**
     * Constructs the class and sets the players list
     * @param users the players of the match
     */
    public Game(List<User> users){
        this.additionalSchemas=false;
        this.users= (ArrayList<User>) users;
        for(User u : users){
            u.setStatus(UserStatus.PLAYING);
        }
        this.board=new Board(users,additionalSchemas);
        this.lockRun = new Object();
        draftedSchemas = board.draftSchemas();
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
    private void waitAction(){
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
     * This method provides the execution order of the game flow
     */
    @Override
    public void run(){
        endLock=false;
        timer = new Timer();
        round = (RoundIterator) this.iterator();

        timer.schedule(new DefaultSchemaAssignment(), MasterServer.getMasterServer().getTurnTime() * 1000);
        waitAction();

        while (round.hasNextRound()){
            board.getDraftPool().draftDice(users.size());

            //Notify to all the users the starting of the round
            for(User u:users){
                u.getServerConn().notifyRoundEvent("start",round.getRoundNumber());
            }

            while(round.hasNext()){
                roundStatus=new RoundStatus();
                endLock=false;
                userPlaying = round.next();

                //Notify to all the users the starting of the turn
                for(User u:users){
                    u.getServerConn().notifyTurnEvent("start",board.getPlayer(userPlaying).getGameId(),round.isFirstTurn()?0:1);
                }

                timer.schedule(new PlayerTurn(), MasterServer.getMasterServer().getTurnTime() * 1000);
                waitAction();

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
            round.nextRound();
        }
    }

    /**
     * Responds to the request by sending one private objective card to the user of the match
     * @param user the user who made the request
     */
    public PrivObjectiveCard getPrivCard(User user){
        return board.getPlayer(user).getPrivObjective();
    }

    /**
     * Responds to the request by sending three public objective cards to the user of the match
     * @param user the user who made the request
     */
    public List<PubObjectiveCard> getPubCards(){
        ArrayList<PubObjectiveCard> cards= new ArrayList<>();

        for (int i=0 ; i < Board.NUM_OBJECTIVES ; i++ ) {
            cards.add(board.getPublicObjective(i));
        }
        return cards;
    }

    /**
     * Responds to the request by sending three tool cards to the user of the match
     * @param user the user who made the request
     */
    public List<ToolCard> getToolCards(){
        ArrayList<ToolCard> cards=new ArrayList<>();

        for (int i = 0; i < Board.NUM_TOOLS; i++) {
            cards.add(board.getToolCard(i));
        }
        return cards;
    }

    /**
     * Responds to the request by sending four schema cards to the user of the match
     * @param user the user who made the request
     */
    public List<SchemaCard> getSchemaCards(User user){
        ArrayList<SchemaCard> schemas=new ArrayList<SchemaCard>();
        for (int i=0 ; i < Board.NUM_PLAYER_SCHEMAS ; i++ ){
            schemas.add(draftedSchemas[(users.indexOf(user)* Board.NUM_PLAYER_SCHEMAS)+i]);
        }
        return schemas;
    }

    /**
     * Responds to the request by sending four schema cards to the user of the match
     * @param user the user who made the request
     * @param playerId the id of the player's desired schema card
     */
    public SchemaCard getUserSchemaCard(int playerId){
        roundStatus.setRequestedSchemaList();
        if(playerId>=0 && playerId<users.size()){
            return board.getPlayer(users.get(playerId)).getSchema();
        }
        return null;
    }

    /**
     * Responds to the request by sending four schema cards to the user of the match
     * @param user the user who made the request
     * @param playerId the id of the player's desired schema card
     */
    public SchemaCard getUserSchemaCard(User user){
        roundStatus.setRequestedSchemaList();
        return board.getPlayer(user).getSchema();
    }

    /**
     * Responds to the request by sending the draftpool to the user of the match
     * @param user the user who made the request
     */
    public List<Die> getDraftedDice(){
        roundStatus.setRequestedDraftPoolList();
        return board.getDraftPool().getDraftedDice();
    }

    /**
     * Responds to the request by sending the list of the dice that are present in the roundTrack (and their relative index)
     * @param user the user who made the request
     */
    public List<List<Die>> getRoundTrackDice(){
        roundStatus.setRequestedRoundTrackList();
        return board.getDraftPool().getRoundTrack().getTrack();
    }

    /**
     * Responds to the request by sending the list of the match's players (and their id inside the Player class)
     * @param user the user who made the request
     */
    public List<Player> getPlayers(){
        ArrayList<Player> players= new ArrayList<>();
        for (User u:users){
            players.add(board.getPlayer(u));
        }
        return players;
    }

    /**
     * Responds by sending the favortokens to the user that made the request
     * @param user the user who made the request
     */
    public int getFavorTokens(User user){
        Player player=board.getPlayer(user);
        if(player.getSchema()!=null){
            return player.getFavorTokens();
        }
        return 0;
    }

    /**
     * Sets the chosen schema card to the user's relative player instance, if all the player have choose a schema card
     * the timer will be stopped
     * @param user the user to set the card
     * @param idSchema the id of the schema card
     */
    public boolean chooseSchemaCard(User user,int idSchema){
        boolean response =false;
        for (SchemaCard s: draftedSchemas){
            if (s.getId()==idSchema){
                response=board.getPlayer(user).setSchema(s);
                break;
            }
        }
        for (User u: users){
            if(board.getPlayer(u).getSchema()==null){
                return response;
            }
        }
        timer.cancel();
        return response;
    }

    public Die selectDie(User user,int index){
        Die die;
        int tempIndex=0;
        if(roundStatus.isRequestedSchemaList()){
            FullCellIterator diceIterator=(FullCellIterator)board.getPlayer(user).getSchema().iterator();
            while(diceIterator.hasNext()){
                die=diceIterator.next().getDie();
                if(tempIndex==index){
                    return die;
                }
                tempIndex++;
            }

            die=board.getPlayer(user).getSchema().getCell(index).getDie();
            roundStatus.setSelectedDie(die);
            return die;
        }
        if(roundStatus.isRequestedDraftPoolList()){
            die=board.getDraftPool().getDraftedDice().get(index);
            roundStatus.setSelectedDie(die);
            return die;
        }
        if(roundStatus.isRequestedRoundTrackList()) {
            List<List<Die>> trackList = getRoundTrackDice();
            ArrayList<Die> dieList;
            int roundN = 0;

            while (tempIndex <= index) {
                dieList = (ArrayList<Die>) trackList.get(roundN);
                for (Die d : dieList) {
                    if (tempIndex == index) {
                        roundStatus.setSelectedDie(d);
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
     * Puts in the user's schemacard the die if possible
     * @param index
     * @return
     */
    public boolean putDie(User user,int index){
        int tempIndex=0;
        if(roundStatus.isSelectedDie()){
            if(roundStatus.isRequestedSchemaList()){
                FullCellIterator diceIterator=(FullCellIterator)board.getPlayer(user).getSchema().iterator();
                while(diceIterator.hasNext()){
                    diceIterator.next();
                    if(tempIndex==index){
                        try {
                            board.getPlayer(user).getSchema().putDie(diceIterator.getIndex(),roundStatus.getSelectedDie());
                            return true;
                        } catch (IllegalDieException e) {
                            return false;
                        }
                    }
                    tempIndex++;
                }
            }
        }
        return false;
    }

    /**
     * Allows the Game model to discard a multiple-message command (for complex actions like putDie(), ToolCard usages)
     */
    public void discard(){
        roundStatus=new RoundStatus();
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