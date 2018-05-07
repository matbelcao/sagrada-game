package it.polimi.ingsw.scorecalculator;
import it.polimi.ingsw.SchemaCard;
import java.util.ArrayList;

/**
 * This class implements the "Column Shade Variety" Public Objective Card
 */
public class ScoreCalculator4 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema) {
        int points = 0;
        int temp;
        ArrayList<Integer> tmpNum = new ArrayList<>();
        boolean badColumn;

        for (int col = 0; col < SchemaCard.NUM_COLS; col++) {
            tmpNum.clear();
            badColumn = false;
            for (int row = 0; row < SchemaCard.NUM_ROWS && !badColumn; row++) {
                if (schema.getCell(row, col).hasDie()) {
                    temp = schema.getCell(row, col).getDie().getShade().toInt();
                    if (schema.getCell(row, col).hasDie() && !tmpNum.contains(temp)) {
                        tmpNum.add(temp);
                    } else {
                        badColumn = true;
                    }
                }else{
                    badColumn = true;
                }
            }
            if (!badColumn) {
                points += 4;
            }
        }
        return points;
    }
}