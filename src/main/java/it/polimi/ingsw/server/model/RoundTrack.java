package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the place where all dice that aren't used during a turn are put, each in a marker
 * corresponding to the turn they where drafted in
 */
public class RoundTrack{
    private List<List<Die>> track;

    /**
     * constructor of the object
     */
    public RoundTrack() {
        this.track = new ArrayList<>(10);

    }

    /**
     * getter of the object
     * @return an Arraylist of Arraylists of dice, that is the round track
     */
    public List<List<Die>> getTrack() {
        return  this.copy();
    }

    private List<List<Die>> copy(){
        List<List<Die>> clone= new ArrayList<>();
        for (int round=0; round <track.size();round++ ){
            clone.add(new ArrayList<>());
            for(int i=0; i<track.get(round).size();i++){
                clone.get(round).add(new Die(track.get(round).get(i).getShade(),track.get(round).get(i).getColor()));
            }
        }
        return clone;
    }

    /**
     * puts a group of dice in a container corresponding to the round they where drafted in
     * @param round the position in the Round Track where the dice are put, corresponding to the current round
     * @param toAdd a list of dice to add to the Round Track
     */
    public void putDice(int round, List<Die> toAdd){
        track.add(round, toAdd);
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

    public void removeDie(int index){
        int i=0;
        for(List<Die> dieList: track) {
            int dieNum=0;
            for (Die d : dieList) {
                if(i==index){
                    dieList.remove(dieNum);
                    return;
                }
                dieNum++;
                i++;
            }
    }
    }
}
