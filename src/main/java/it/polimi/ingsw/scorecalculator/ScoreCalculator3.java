package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.SchemaCard;

import java.util.ArrayList;

/**
 * "Row Shades" card implementation
 */
public class ScoreCalculator3 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema) {
        int points = 0;
        ArrayList<Integer> tmpNum = new ArrayList();
        boolean badRow;

        for (int row = 0; row < 4; row++) {
            tmpNum.clear();
            badRow = false;
            for (int col = 0; col < 5 && !badRow; col++) {
                if (schema.getCell(row, col).hasDie() && !tmpNum.contains(schema.getCell(row, col).getDie().getShade().toInt())) {
                    tmpNum.add(schema.getCell(row, col).getDie().getShade().toInt());
                } else {
                    badRow = true;
                }
            }
            if (!badRow) {
                points += 5;
            }
        }
        return points;
    }
}