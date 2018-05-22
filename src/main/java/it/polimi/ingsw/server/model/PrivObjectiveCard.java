package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

/**
 * This class implements the Cards named "Private Objective" and their score calculating algorithms
 */
public class PrivObjectiveCard extends Card{
    private Color color;
    static final int NUM_PRIV_OBJ=5;

    /**
     * Constructs the card setting its id, name and description
     * @param id the id of the card
     * @param xmlSrc the address to the xml file containing necessary information to initialize the cards
     */
    public PrivObjectiveCard(int id, String xmlSrc){
        super();
        this.color=Color.valueOf(super.xmlReader(id,xmlSrc,"PrivObjectiveCard"));
    }

    /**
     * Returns the Color object of the card
     * @return the Color of the card
     */
    public Color getColor(){
        return this.color;
    }

    /**
     * This method computes the score given by the private objective card (based on its color)
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
            if(color.toString().equals(tempDie.getColor().toString())){
                points+=tempDie.getShade().toInt();
            }
        }
        return points;
    }
}