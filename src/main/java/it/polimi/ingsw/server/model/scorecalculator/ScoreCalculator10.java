package it.polimi.ingsw.server.model.scorecalculator;
import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
import it.polimi.ingsw.server.model.SchemaCard;

/**
 * This class implements the "DieColor Variety" Public Objective Card
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
        int[] count = new int[DieColor.values().length -1];
        FullCellIterator diceIterator = (FullCellIterator) schema.iterator();
        Die die;

        while(diceIterator.hasNext()){
            die=diceIterator.next().getDie();
            count[die.getColor().ordinal()] += 1;
        }
        points=count[0];
        for(Integer i : count){
           if(points>i){ points=i;}
        }
        return points*4;
    }

}
