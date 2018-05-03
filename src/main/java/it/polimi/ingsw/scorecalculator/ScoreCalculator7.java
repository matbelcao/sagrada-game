package it.polimi.ingsw.scorecalculator;

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
        int FIVE=0,SIX=0;
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()){
            diceIterator.next();
            if(schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShadeInt()==5){
                FIVE++;
            }
            if(schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShadeInt()==6){
                SIX++;
            }
        }
        return Math.min(FIVE,SIX);
    }
}