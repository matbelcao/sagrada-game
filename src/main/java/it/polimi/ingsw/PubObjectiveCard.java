package it.polimi.ingsw;

import it.polimi.ingsw.scorecalculator.ScoreCalculator;

import java.lang.reflect.InvocationTargetException;

public class PubObjectiveCard extends Card{
    private ScoreCalculator scoreCalculator;

    public PubObjectiveCard(int id, String xmlSrc){
        super();
        super.xmlReader(id,xmlSrc,"PubObjectiveCard");

        String className = "ScoreCalculator" + id;
        String fullPathOfTheClass = "it.polimi.ingsw.scorecalculator." + className;
        Class cls = null;
        try {
            cls = Class.forName(fullPathOfTheClass);
            assert cls != null;
            this.scoreCalculator = (ScoreCalculator) cls.getDeclaredConstructor().newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public int getCardScore(SchemaCard schema){
        return scoreCalculator.calculateScore(schema);
    }
}