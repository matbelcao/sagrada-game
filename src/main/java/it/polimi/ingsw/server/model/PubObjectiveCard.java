package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.scorecalculator.ScoreCalculator;

import java.lang.reflect.InvocationTargetException;

/**
 * This class implements the Cards named "Public Objective" and their score calculating algorithms
 */
public class PubObjectiveCard extends Card{
    private ScoreCalculator scoreCalculator;
    public static final int NUM_PUB_OBJ=10;
    /**
     * Constructs the card setting its id, name, description and score calculating algorithm
     * @param id the id of the card
     * @param xmlSrc the address to the xml file containing necessary information to initialize the cards
     */
    public PubObjectiveCard(int id, String xmlSrc){
        super();
        super.xmlReader(id,xmlSrc,"PubObjectiveCard");

        String className = "ScoreCalculator" + id;
        String fullPathOfTheClass = "it.polimi.ingsw.server.model.scorecalculator." + className;
        Class cls = null;
        try {
            cls = Class.forName(fullPathOfTheClass);
            assert cls != null;
            this.scoreCalculator = (ScoreCalculator) cls.getDeclaredConstructor().newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
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