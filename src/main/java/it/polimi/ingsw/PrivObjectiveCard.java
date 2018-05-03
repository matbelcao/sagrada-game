package it.polimi.ingsw;

public class PrivObjectiveCard extends Card{
    private Color color;

    public PrivObjectiveCard(int id, String xmlSrc){
        super();
        this.color=Color.valueOf(super.xmlReader(id,xmlSrc,"PrivObjectiveCard"));
    }

    public String getColor(){
        return color.toString();
    }


    public int getCardScore(SchemaCard schema){
        int points=0;
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()){
            diceIterator.next();
            if(color.toString().equals(schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getColor())){
                points+=schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie().getShadeInt();
            }
        }
        return points;
    }
}