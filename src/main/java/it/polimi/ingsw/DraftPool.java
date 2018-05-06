package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class represents the area where drafted dice are put
 */
public class DraftPool {
    ArrayList<Die> drafted;

    /**
     * Constructor of the class
     */
    DraftPool(){
        this.drafted = new ArrayList<>();
    }
    /**
     * Adds a die to the draft area
     * @param die the die to be added
     */
    void addDie(Die die){drafted.add(die);}

    /**
     * Adds a group of dice to the draft area, during a game this can happen only after drafting
     * from the dice bag, when the draft area is empty
     * @param dice the dice to be added
     */
    void addDice(ArrayList<Die> dice){
        assert(this.drafted.isEmpty());
        this.drafted.addAll(dice);
    }

    /**
     * This method returns the die selected through the index and removes it from the draft area
     * @param index the index of the selected die
     * @return the Die
     */
    Die chooseDie(int index){
        Die die;
        assert index>=0 && index<this.drafted.size();
        die = this.drafted.get(index);
        this.drafted.remove(index);
        return die;
    }

    /**
     * @return  a reference to all the dice in the draft area
     */
    ArrayList<Die> getDice(){ return this.drafted; }
    
}
