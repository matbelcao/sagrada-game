package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.FullCellIterator;
import it.polimi.ingsw.SchemaCard;

/**
 * "Medium Shades" card implementation
 */
public class ScoreCalculator6 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema){
        int threes=0,fours=0;
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()){
            diceIterator.next();
            if(schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShade().toInt()==3){
                threes++;
            }
            if(schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShade().toInt()==4){
                fours++;
            }
        }
        return Math.min(threes,fours)*2;
    }
}