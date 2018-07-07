package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.scorecalculator.ScoreCalculator;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Cards named "Public Objective" and their score calculating algorithms
 */
public class PubObjectiveCard extends Card{
    private static final String SCORE_CALCULATOR = "ScoreCalculator";
    private static final String PUB_OBJECTIVE_CARD = "PubObjectiveCard";
    private static final String PATH_TO_CLASS = "it.polimi.ingsw.server.model.scorecalculator.";
    private ScoreCalculator scoreCalculator;
    static final int NUM_PUB_OBJ=10;
    /**
     * Constructs the card setting its id, name, description and score calculating algorithm
     * @param id the id of the card
     * @param xmlSrc the address to the xml file containing necessary information to initialize the cards
     */
    public PubObjectiveCard(int id, String xmlSrc){
        super();
        if (!(id<=NUM_PUB_OBJ && id>=1)){throw new IllegalArgumentException();}
        super.xmlReader(id,xmlSrc, PUB_OBJECTIVE_CARD);

        String className = SCORE_CALCULATOR + id;
        String fullPathOfTheClass = PATH_TO_CLASS + className;
        Class cls = null;

        try {
            cls = Class.forName(fullPathOfTheClass);
        } catch (ClassNotFoundException e) {
            Logger.getGlobal().log(Level.INFO,e.getMessage());
        }

        try {
            if(cls!=null){ this.scoreCalculator = (ScoreCalculator) cls.getDeclaredConstructor().newInstance(); }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Logger.getGlobal().log(Level.INFO,e.getMessage());
        }

    }


    /**
     * this method calls the card-specific method for calculating the score obtained
     * @param schema the schema card the score needs to be calculated on
     * @return the actual score given by the card
     */
    public int getCardScore(SchemaCard schema){
        return scoreCalculator.calculateScore(schema);
    }
}