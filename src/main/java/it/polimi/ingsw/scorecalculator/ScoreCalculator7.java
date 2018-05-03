package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.Die;
import it.polimi.ingsw.FullCellIterator;
import it.polimi.ingsw.SchemaCard;

/**
 * "Dark Shades" card implementation
 */
public class ScoreCalculator7 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema){
        int fives=0,sixes=0;
        Die die;

        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()){
            die=diceIterator.next().getDie();

            if(die.getShade().toInt()==5){
                fives++;
            }
            if(die.getShade().toInt()==6){
                sixes++;
            }
        }
        return Math.min(fives,sixes)*2;
    }
}