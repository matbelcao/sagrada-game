package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class implements an iterator on the players following Sagrada's rules for round management
 */
public class RoundIterator implements Iterator<User> {
    private final Integer numUsers;
    private int i;
    private ArrayList<User> users;
    private User next;
    private int round;

    /**
     * Constructs the iterator initializing it as necessary
     * @param users the List of users that are playing the match
     */
    RoundIterator(List<User> users){
        this.users=(ArrayList<User>) users;
        this.numUsers=users.size();
        this.next=null;
        this.i=0;
        this.round=-1;
    }

    /**
     * Resets variables to begin a new round
     */
    public void nextRound(){
        if(hasNextRound()) {
            this.next = null;
            this.i = 0;
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
        if(i<2*numUsers){
            if(i<numUsers){
                next=users.get((round + i)%numUsers);
            }else{
                next=users.get((numUsers - 1 + round - i%numUsers)%numUsers);
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
    public boolean isFirstTurn(){ return i<=numUsers;}

    /**
     * Gets the next player in the round
     * @return the next player
     */
    @Override
    public User next(){

        if(this.hasNext()){ i++; return next;}
        throw new NoSuchElementException();
    }
}
