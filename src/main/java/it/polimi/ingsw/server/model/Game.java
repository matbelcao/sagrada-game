package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.connection.User;
import it.polimi.ingsw.common.enums.UserStatus;
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
    private Boolean endTurn;
    private final Object lockObj;
    private User userPlaying;
    private Timer timer;

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
        this.lockObj = new Object();

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
        }
    }

    /**
     * Allows the enabled user to perform the desired actions during his turn ( until it isn't executed )
     */
    private class PlayerTurn extends TimerTask {
        @Override
        public void run(){
            synchronized (lockObj) {
                endTurn = true;
                //endTurn.notifyAll();
            }
        }
    }

    /**
     * This method provides the execution order of the game flow
     */
    @Override
    public void run(){
        sendCards();
        timer = new Timer();
        timer.schedule(new DefaultSchemaAssignment(), MasterServer.getMasterServer().getTurnTime() * 1000);
        round = (RoundIterator) this.iterator();
        while (round.hasNextRound()){
            while(round.hasNext()){
                userPlaying = round.next();
                endTurn=false;
                timer.schedule(new PlayerTurn(), MasterServer.getMasterServer().getTurnTime() * 1000);
                synchronized (lockObj) {
                    while (!endTurn) {
                        /*try {
                            endTurn.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    }
                }
            }
            round.nextRound();
        }
    }

    /**
     * Sends four schema cards for each user of the match, three public objectives and one private objective
     */
    private void sendCards(){
        int i;
        draftedSchemas = board.draftSchemas();

        for (User u: users){
            for ( i=0 ; i < Board.NUM_PLAYER_SCHEMAS ; i++ ){
                u.getServerConn().notifySchema(draftedSchemas[(users.indexOf(u)* Board.NUM_PLAYER_SCHEMAS)+i]);
            }
            for ( i=0 ; i < Board.NUM_TOOLS ; i++ ){
                u.getServerConn().notifyToolCard(board.getToolCard(i));
            }
            for ( i=0 ; i < Board.NUM_OBJECTIVES ; i++ ){
                u.getServerConn().notifyPublicObjective(board.getPublicObjective(i));
            }
            u.getServerConn().notifyPrivateObjective(board.getPlayer(u).getPrivObjective());
        }
    }

    /**
     * Sets the chosen schema card to the user's relative player instance, if all the player have choose a schema card
     * the timer will be stopped
     * @param user the user to set the card
     * @param idSchema the id of the schema card
     */
    public void chooseSchemaCard(User user,int idSchema){
        for (SchemaCard s: draftedSchemas){
            if (s.getId()==idSchema){
                board.getPlayer(user).setSchema(s);
                break;
            }
        }
        for (User u: users){
            if(board.getPlayer(u).getSchema()==null){
                return;
            }
        }
        timer.cancel();
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
     * Constructs the class and sets the players list
     * @param users the players of the match
     */
    public Game(List<User> users){
        this.additionalSchemas=false;
        this.users= (ArrayList<User>) users;
        for(User u : users){
            u.setStatus(UserStatus.PLAYING);
        }
        this.lockObj = new Object();;
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