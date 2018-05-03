package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.FullCellIterator;
import it.polimi.ingsw.SchemaCard;

/**
 * "Light Shades" card implementation
 */
public class ScoreCalculator5 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema){
        int ONE=0,TWO=0;
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()){
            diceIterator.next();
            if(schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShade().toInt()==1){
                ONE++;
            }
            if(schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShade().toInt()==2){
                TWO++;
            }
        }
        return Math.min(ONE,TWO)*2;
    }
}