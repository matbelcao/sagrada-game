package it.polimi.ingsw.server.model.scorecalculator;
import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.enums.Face;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
import it.polimi.ingsw.server.model.SchemaCard;

/**
 * This class implements the "Shade Variety" Public Objective Card
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
            die=diceIterator.next().getDie();
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