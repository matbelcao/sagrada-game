package it.polimi.ingsw;

import it.polimi.ingsw.scorecalculator.ScoreCalculator;

public class PubObjectiveCard extends Card{
    private ScoreCalculator scoreCalculator;

    public PubObjectiveCard(int id, String xmlSrc){
        super();
        super.xmlReader(id,xmlSrc,"PubObjectiveCard");
    }

    public int getCardScore(SchemaCard schema){
        return scoreCalculator.calculateScore(schema);
    }
}