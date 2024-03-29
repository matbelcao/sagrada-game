package it.polimi.ingsw.server.model.iterators;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.controller.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class implements an iterator on the players following Sagrada's rules for round management
 */
public class RoundIterator implements Iterator<User> {
    private int turnNumberInRound;
    private ArrayList<User> users;
    private User next;
    private int round;

    /**
     * Constructs the iterator initializing it as necessary
     * @param users the List of users that are playing the match
     */
    public RoundIterator(List<User> users){
        this.users=(ArrayList<User>) users;

        this.next=null;
        this.turnNumberInRound =0;
        this.round=-1;
    }

    /**
     * Resets variables to begin a new round
     */
    public void nextRound(){
        if(hasNextRound()) {
            this.next = null;
            this.turnNumberInRound = 0;
            this.round++;
        }else{
            throw new NoSuchElementException("This is the last Round, there are no more rounds to go!");
        }
    }

    /**
     * Returns the number of the turn that is being played (0 to 9)
     * @return the round's number
     */
    public int getRoundNumber(){
        if(this.round==-1){this.nextRound();}
        return this.round;
    }

    /**
     * This method checks whether the round is over
     * @return true iff the round is not over
     */
    @Override
    public boolean hasNext() {
        if(this.round==-1){this.nextRound();}
        if(turnNumberInRound <2*users.size()){
            if(turnNumberInRound <users.size()){
                next=users.get((round + turnNumberInRound)%users.size());
            }else{
                next=users.get((users.size() - 1 + round - turnNumberInRound %users.size())%users.size());
            }
            return true;
        }
        return false;
    }

    /**
     * This method checks whether there are more rounds to go or not
     * @return true iff this is not the last round
     */
    public boolean hasNextRound() {
        return this.round < Board.NUM_ROUNDS - 1;
    }

    /**
     * Checks whether the u are playing the first or the second turn of the round
     * @return true iff there's at least one player that
     */
    public boolean isFirstTurn(){ return turnNumberInRound <=users.size();}


    /**
     * Gets the next player in the round
     * @return the next player
     */
    @Override
    public User next(){

        if(this.hasNext()){ turnNumberInRound++; return next;}
        throw new NoSuchElementException();
    }
}
