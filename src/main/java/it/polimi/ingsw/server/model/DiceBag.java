package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.server.model.exceptions.EmptyDiceBagException;

import java.util.ArrayList;
import java.util.List;
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
    public DiceBag(){
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
        if(!toDraft.isEmpty())
            return toDraft.remove(randomGen.nextInt(toDraft.size()));
        else
            throw new EmptyDiceBagException();
    }
    /**
     * @param quantity the quantity of dice to be drafted at once
     * @return an ArrayList containing the drafted dice
     */
    public List<Die> draftDice(int quantity) throws EmptyDiceBagException {
        ArrayList<Die> drafted = new ArrayList<>();
        while(quantity > 0){
            drafted.add(draftDie());
            quantity--;
        }
        return drafted;
    }

    /**
     * Puts a die in the DiceBag, to be used by tool card #11
     * @param die to be rerolled
     */
    public void putDie(Die die){
        Die.reroll(die);
        toDraft.add(die);
    }

}
