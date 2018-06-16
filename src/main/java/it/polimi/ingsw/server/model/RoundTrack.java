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
     * Returns a list of dices list (one foreach round)
     * @return a List of List of dice, that is the round track
     */
    public List<List<Die>> getTrack() {
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
     * Returns a list of the dice contained in the RoundTrack
     * @return a list of Die
     */
    public List<Die> getTrackList(){
        List<Die> list= new ArrayList<>();

        for(List<Die> dieList: track) {
            for (Die d : dieList) {
                list.add(d);
            }
        }
        return list;
    }


    /**
     * Puts a group of dice in a container corresponding to the round they where drafted in
     * @param round the position in the Round Track where the dice are put, corresponding to the current round
     * @param toAdd a list of dice to add to the Round Track
     */
    public void putDice(int round, List<Die> toAdd){
        track.add(round, toAdd);
    }

    /**
     * Removes one Die from the RoundTrack
     * @param index the index of the die to remove
     */
    public void removeDie(int index){
        assert index>=0 && index<getTrackList().size();
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
