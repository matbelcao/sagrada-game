package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.UserStatus;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.PubObjectiveCard;
import it.polimi.ingsw.server.model.SchemaCard;
import it.polimi.ingsw.server.model.ToolCard;
import it.polimi.ingsw.server.model.iterators.RoundIterator;
import org.jetbrains.annotations.NotNull;

import javax.jws.soap.SOAPBinding;
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

    /**
     * Constructs the class and sets the players list
     * @param users the players of the match
     * @param additionalSchemas true if additional are wanted by the user
     */
    public Game(List<User> users,boolean additionalSchemas){
        if(additionalSchemas){

            // TODO: 21/05/2018


            System.out.print("additional schemas");
        }
        this.users= (ArrayList<User>) users;
        for(User u : users){
            u.setStatus(UserStatus.PLAYING);
            u.setGame(this);
            u.getServerConn().notifyGameStart(users.size(), users.indexOf(u));
        }
        board=new Board(users);
    }

    /**
     * This method provides the execution order of the game flow
     */
    @Override
    public void run(){
        try {
            sendSchemaCards();
            Thread.sleep(MasterServer.timeGame*1000);
            defaultSchemaCardAssignment();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }

    /**
     * Sends four schema cards for each user of the match
     */
    private void sendSchemaCards(){
        draftedSchemas = board.draftSchemas();
        for (User u: users){
            for (int i=(users.indexOf(u)*4);i<(users.indexOf(u)*4)+4;i++){
                //u.getServerConn().notifySchema(draftedSchemas[i]);
            }
        }
    }

    /**
     * Sets the chosen schema card to the user's relative player instance
     * @param user the user to set the card
     * @param idSchema the id of the schema card
     * @throws InterruptedException if all the players have chosen the schema card
     */
    public void chooseSchemaCard(User user,int idSchema) throws InterruptedException{
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
        throw  new InterruptedException();
    }

    public void defaultSchemaCardAssignment(){
        for (User u:users){
            //if(board.getPlayer())

        }
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
    }

    /**
     * Instantiates a new board for the match
     */
    public void createBoard(){
        this.board= new Board(this.users);
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