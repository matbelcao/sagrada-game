package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.*;

/**
 * This class implements the procedure needed to calculate the score according to the tenth public objective
 */

public class ScoreCalculator10 implements ScoreCalculator{
    /**
     * Calculates the score due to the tenth card among the public objectives
     * @param schema the schema whom score needs to be calculated
     * @return the actual score
     */
    @Override
    public int calculateScore(SchemaCard schema) {
        int points;
        int[] count = new int[Color.values().length];
        FullCellIterator diceIterator = (FullCellIterator) schema.iterator();
        Die die;

        while(diceIterator.hasNext()){
            diceIterator.next();
            die=schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie();
            count[die.getColor().ordinal()] += 1;
        }
        points=count[0];
        for(Integer i : count){
           if(points>i){ points=i;}
        }
        return points*4;
    }

}
