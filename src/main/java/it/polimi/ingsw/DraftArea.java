package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class represents the area where drafted dice are put
 */
public class DraftArea {
    ArrayList<Die> drafted;

    /**
     * Constructor of the class
     */
    DraftArea(){
        drafted = new ArrayList<Die>();
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
        assert(drafted==null);
        drafted.addAll(dice);
    }
    /**
     * @return  a reference to all the dice in the draft area
     */
    ArrayList<Die> getDice(){ return drafted; }
    
}
