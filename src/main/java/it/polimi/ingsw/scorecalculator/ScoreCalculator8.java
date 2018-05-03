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
    public int calculateScore(SchemaCard schema) {
        int[] count = {0, 0, 0, 0, 0, 0};
        int min;

        FullCellIterator diceIterator = (FullCellIterator) schema.iterator();

        while (diceIterator.hasNext()) {
            diceIterator.next();
            count[schema.getCell(diceIterator.getRow(), diceIterator.getColumn()).getDie().getShade().ordinal()]++;
        }
        min = count[0];
        for (int x : count) {
            if (x < min) {
                min = x;
            }
        }

        return min;
    }
}