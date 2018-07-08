package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

/**
 * This class implements the Cards named "Private Objective" and their score calculating algorithms
 */
public class PrivObjectiveCard extends Card{
    private static final String PRIV_OBJECTIVE_CARD = "PrivObjectiveCard";
    private DieColor dieColor;
    static final int NUM_PRIV_OBJ=5;
    private static final String XML_SOURCE=MasterServer.XML_SOURCE+"PrivObjectiveCard.xml";

    /**
     * Constructs the card setting its id, name and description
     * @param id the id of the card
     */
    public PrivObjectiveCard(int id){
        super();
        this.dieColor =DieColor.valueOf(super.xmlReader(id,XML_SOURCE, PRIV_OBJECTIVE_CARD));
    }

    /**
     * Returns the DieColor object of the card
     * @return the DieColor of the card
     */
    public DieColor getDieColor(){
        return this.dieColor;
    }

    /**
     * This method computes the score given by the private objective card (based on its dieColor)
     * @param schema the schema card the score needs to be calculated on
     * @return the actual score given by the card
     */
    public int getCardScore(SchemaCard schema){
        int points=0;
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();
        Die tempDie;

        while(diceIterator.hasNext()){
            diceIterator.next();
            tempDie=schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie();
            if(dieColor.toString().equals(tempDie.getColor().toString())){
                points+=tempDie.getShade().toInt();
            }
        }
        return points;
    }
}