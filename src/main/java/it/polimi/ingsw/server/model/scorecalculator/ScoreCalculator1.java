package it.polimi.ingsw.server.model.scorecalculator;
import it.polimi.ingsw.server.model.SchemaCard;
import java.util.ArrayList;

/**
 * This class implements the "Row DieColor Variety" Public Objective Card
 */
public class ScoreCalculator1 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema) {
        int points = 0;
        ArrayList<String> tmpNum = new ArrayList<>();
        boolean badRow;
        String temp;

        for (int row = 0; row < SchemaCard.NUM_ROWS; row++) {
            tmpNum.clear();
            badRow = false;
            for (int col = 0; col < SchemaCard.NUM_COLS && !badRow; col++) {
                if (schema.getCell(row, col).hasDie()){
                    temp=schema.getCell(row, col).getDie().getColor().toString();
                    if (!tmpNum.contains(temp)) {
                        tmpNum.add(temp);
                    } else {
                        badRow = true;
                    }
                }else{
                    badRow = true;
                }
            }
            if (!badRow) {
                points += 6;
            }
        }
        return points;
    }
}
