package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.*;

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
        int[] count = new int[Face.values().length];
        int min;
        Die die;
        FullCellIterator diceIterator = (FullCellIterator) schema.iterator();

        while (diceIterator.hasNext()) {
            diceIterator.next();
            die=schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie();
            count[die.getShade().ordinal()]+=1;
        }
        min = count[0];
        for (int x : count) {
            if (x < min) {
                min = x;
            }
        }

        return min*5;
    }
}