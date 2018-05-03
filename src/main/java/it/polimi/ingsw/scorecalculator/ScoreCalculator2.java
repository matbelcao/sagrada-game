package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.SchemaCard;

import java.util.ArrayList;
/**
 * This class implements the "Different Colors - Column " card Public Objective Card
 */
public class ScoreCalculator2 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema){
        int points = 0;
        ArrayList<String> tmpNum = new ArrayList<>();
        boolean badRow;
        String temp;

        for (int col = 0; col < SchemaCard.NUM_COLS;col++) {
            tmpNum.clear();
            badRow = false;
            for ( int row = 0; row < SchemaCard.NUM_ROWS && !badRow; row++) {
                temp=schema.getCell(row, col).getDie().getColor().toString();
                if (schema.getCell(row, col).hasDie() && !tmpNum.contains(temp)) {
                    tmpNum.add(temp);
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