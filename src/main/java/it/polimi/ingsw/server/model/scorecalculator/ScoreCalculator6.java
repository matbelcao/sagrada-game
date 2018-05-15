package it.polimi.ingsw.server.model.scorecalculator;
import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
import it.polimi.ingsw.server.model.SchemaCard;

/**
 * This class implements the "Medium Shades" Public Objective Card
 */
public class ScoreCalculator6 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema){
        int threes=0,fours=0;
        Die die;
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()){
            die=diceIterator.next().getDie();

            if(die.getShade().toInt()==3){
                threes++;
            }
            if(die.getShade().toInt()==4){
                fours++;
            }
        }
        return Math.min(threes,fours)*2;
    }
}