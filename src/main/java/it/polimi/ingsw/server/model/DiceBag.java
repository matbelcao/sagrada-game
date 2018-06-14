package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Face;
import it.polimi.ingsw.server.model.exceptions.EmptyDiceBagException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the container of all the dice available for drafting during a game,
 * it provides methods for randomly drafting dice
 */
public class DiceBag {
    private List<Die> toDraft;

    /**
     * Constructs the object adding all 90 dice at once setting their shade randomly
     */
    public DiceBag(){
        toDraft = new ArrayList<>();
        int randomShade;
        Random randomGen = new Random();
        for (int color=0;color<5;color ++) {
            for (int i = 0; i < 18; i++) {
                randomShade = randomGen.nextInt(6) ;
                    toDraft.add(new Die(Face.values()[randomShade],Color.values()[color]));
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
     * @return a List containing the drafted dice
     */
    public List<Die> draftDice(int quantity) {
        List<Die> drafted = new ArrayList<>();
        try {
            while(quantity > 0) {

                drafted.add(draftDie());
                quantity--;
            }
        }catch (EmptyDiceBagException e) {
            return drafted;
        }
        return drafted;
    }

    /**
     * Puts a die in the DiceBag, to be used by tool card #11
     */
    public Die substituteDie(Die oldDie){
        try {
            Die newDie=draftDie();
            oldDie.setShade(newDie.getShade().toInt());
            oldDie.setColor(newDie.getColor().toString());
            toDraft.add(newDie);
        } catch (EmptyDiceBagException e) {
            return oldDie;
        }
        return oldDie;
    }

}
