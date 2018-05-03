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
        int THREE=0,FOUR=0;
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()){
            diceIterator.next();
            if(schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShadeInt()==3){
                THREE++;
            }
            if(schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShadeInt()==4){
                FOUR++;
            }
        }
        return Math.min(THREE,FOUR);
    }
}