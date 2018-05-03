package it.polimi.ingsw.scorecalculator;
import it.polimi.ingsw.SchemaCard;

/**
 * This is the interface for the strategy pattern used to calculate the different Public Objective scores
 */
public interface ScoreCalculator {
    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    public int calculateScore(SchemaCard schema);
}
