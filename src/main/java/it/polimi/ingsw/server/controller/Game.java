package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.PubObjectiveCard;
import it.polimi.ingsw.server.model.iterators.RoundIterator;
import it.polimi.ingsw.server.model.ToolCard;
import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.UserStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * This class represents the controller of the game. It manages the rounds and the operations that the players make on the board
 */
public class Game extends Thread implements Iterable  {
    public static final int NUM_ROUND=10;
    private Board board;
    private boolean additionalSchemas; //to be used for additional schemas FA
    private ArrayList<User> users;

    /**
     * Constructs the class and sets the players list
     * @param users the players of the match
     * @param additionalSchemas true if additional are wanted by the user
     */
    public Game(List<User> users,boolean additionalSchemas){
        if(additionalSchemas){

            //PLACEHOLDER


            System.out.print("additional schemas");
        }
        this.users= (ArrayList<User>) users;
        for(User u : users){
            u.setStatus(UserStatus.PLAYING);
            u.setGame(this);
            u.getServerConn().notifyGameStart(users.size(), users.indexOf(u));
        }
    }

    /**
     * Notify to the active users that an user has been reconnected to the game
     * @param user the user to notify
     */
    public void notifyReconnectedUser(User user){
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
    public void notifyDisconnectedUser(User user){
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
    public void notifyQuittedUser(User user){
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
        this.board= new Board(this.users,draftPubObjectives(),draftToolCards());
    }

    /**
     * Selects random ToolCards to be used in the match
     * @return an array containing the tools
     */
    private ToolCard[] draftToolCards() {

        ToolCard[] toolCards= new ToolCard[Board.NUM_TOOLS];
        Random randomGen = new Random();
        for(int i =0; i<Board.NUM_TOOLS;i++){
            toolCards[i]=new ToolCard(randomGen.nextInt(ToolCard.NUM_TOOL_CARDS) + 1, MasterServer.XML_SOURCE+"ToolCard.xml");
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
        for(int i =0; i<Board.NUM_OBJECTIVES;i++){
            pubObjectiveCards[i]=new PubObjectiveCard(randomGen.nextInt(PubObjectiveCard.NUM_PUB_OBJ) + 1,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        }
        return pubObjectiveCards;
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
        return new RoundIterator(board.getUsers());
    }

}