package it.polimi.ingsw.scorecalculator;
import it.polimi.ingsw.Die;
import it.polimi.ingsw.FullCellIterator;
import it.polimi.ingsw.SchemaCard;

/**
 * This class implements the "Light Shades" Public Objective Card
 */
public class ScoreCalculator5 implements ScoreCalculator{

    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema){
        int ones=0,twos=0;
        Die die;
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()){
            die=diceIterator.next().getDie();

            if(die.getShade().toInt()==1){
                ones++;
            }
            if(die.getShade().toInt()==2){
                twos++;
            }
        }
        return Math.min(ones,twos)*2;
    }
}