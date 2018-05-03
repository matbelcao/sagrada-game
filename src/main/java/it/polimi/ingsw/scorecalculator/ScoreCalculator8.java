package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.FullCellIterator;
import it.polimi.ingsw.SchemaCard;

/**
 * "Different Shades" card implementation
 */
public class ScoreCalculator8 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema){
        int [] num = {0,0,0,0,0,0};
        int min;

        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()){
            diceIterator.next();
            switch (schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShadeInt()){
                case 1:
                    num[0]++;
                    break;
                case 2:
                    num[1]++;
                    break;
                case 3:
                    num[2]++;
                    break;
                case 4:
                    num[3]++;
                    break;
                case 5:
                    num[4]++;
                    break;
                case 6:
                    num[5]++;
                    break;
                default:
                    break;
            }
        }

        min=num[0];
        for(int  x : num) {
            if ( x < min){
                min = x;
            }
        }

        return min;
    }
}