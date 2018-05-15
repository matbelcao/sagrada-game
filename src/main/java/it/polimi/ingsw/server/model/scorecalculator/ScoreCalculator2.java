package it.polimi.ingsw.server.model.scorecalculator;
import it.polimi.ingsw.server.model.SchemaCard;
import java.util.ArrayList;

/**
 * This class implements the "Column Color Variety" card Public Objective Card
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
        boolean badColumn;
        String temp;

        for (int col = 0; col < SchemaCard.NUM_COLS;col++) {
            tmpNum.clear();
            badColumn = false;
            for ( int row = 0; row < SchemaCard.NUM_ROWS && !badColumn; row++) {
                if (schema.getCell(row, col).hasDie()){
                    temp=schema.getCell(row, col).getDie().getColor().toString();
                    if (!tmpNum.contains(temp)) {
                        tmpNum.add(temp);
                    } else {
                        badColumn = true;
                    }
                }else{
                    badColumn = true;
                }
            }
            if (!badColumn) {
                points += 5;
            }
        }
        return points;
    }
}