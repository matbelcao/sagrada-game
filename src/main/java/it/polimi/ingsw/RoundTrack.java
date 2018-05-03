package it.polimi.ingsw;

import java.util.ArrayList;
/**
 * This is the place where all dice that aren't used during a turn are put, each in a marker
 * corresponding to the turn they where drafted in
 */
public class RoundTrack{
    private ArrayList<ArrayList<Die>> track;

    /**
     * constructor of the object
     */
    public RoundTrack() {
        this.track = new ArrayList<ArrayList<Die>>(10);
    }
    /**
     * getter of the object
     * @return an Arraylist of Arraylists of dice, that is the round track
     */
    public ArrayList<ArrayList<Die>> getTrack() {
        return track;   //WARNING: Modifying the returned object will modify the RoundTrack
    }
    /**
     * puts a group of dice in a container corresponding to the round they where drafted in
     * @param round the position in the Round Track where the dice are put, corresponding to the current round
     * @param toAdd a list of dice to add to the Round Track
     */
    public void putDice(int round, ArrayList<Die> toAdd){
        assert track.get(round).isEmpty(); //adding the dice to an empty container in the track
        track.set(round,toAdd);
    }
    /**
     * swaps a die from the Board with a die of the Round Track used by tool card #5
     * @param round the position in the Round Track of the dice to be swapped
     * @param pickedDiePosition the position in the stack of dice of the dice picked for swapping
     * @param dieToPut the die to put in the Round Track from the Board
     * @return the die chosen to be put in the Board from the Round Track
     */
    public Die swapDie(int round,int pickedDiePosition, Die dieToPut) {
        track.get(round).add(dieToPut);
        return track.get(round).remove(pickedDiePosition);
    }
}
