package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Random;

/**
 * This is the container of all the dice available for drafting during a game,
 * it provides methods for randomly drafting dice
 */
public class DiceBag {
    private ArrayList<Die> toDraft;

    /**
     * Constructs the object adding all 90 dice at once setting their shade randomly
     */
    DiceBag(){
        toDraft = new ArrayList<>();
        int randomShade;
        Random randomGen = new Random();
        for (Color color : Color.values()) {
            for (int i = 0; i < 18; i++) {
                randomShade = randomGen.nextInt(6) + 1;
                    toDraft.add(new Die(randomShade,color.toString()));
            }
        }
    }
    /**
     * @return a die from the DiceBag removing it from the former's list
     */
    public Die draftDie() throws EmptyDiceBagException {
        Random randomGen = new Random();
        if(toDraft.size()>0)
            return toDraft.remove(randomGen.nextInt(toDraft.size()));
        else
            throw new EmptyDiceBagException();
    }
    /**
     * Puts a die in the DiceBag, to be used by tool card #11
     */
    public void putDie(Die die){
        toDraft.add(die);
    }

}
