package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.Cell;
import it.polimi.ingsw.Color;
import it.polimi.ingsw.FullCellIterator;
import it.polimi.ingsw.SchemaCard;

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
        Integer[] count;
        FullCellIterator fullCell = (FullCellIterator) schema.iterator();
        Cell next;

        count = new Integer[Color.values().length];

        while(fullCell.hasNext()){
            next=fullCell.next();
            count[next.getDie().getColor().ordinal()] += 1;
        }
        points=count[0];
        for(Integer i : count){
           if(points>i){ points=i;}
        }
        return points*Color.values().length;
    }

}
