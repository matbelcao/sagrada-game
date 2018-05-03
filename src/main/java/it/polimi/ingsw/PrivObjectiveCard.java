package it.polimi.ingsw;

public class PrivObjectiveCard extends Card{
    private Color color;

    public PrivObjectiveCard(int id, String xmlSrc){
        super();
        this.color=Color.valueOf(super.xmlReader(id,xmlSrc,"PrivObjectiveCard"));
    }

    public Color getColor(){
        return this.color;
    }


    public int getCardScore(SchemaCard schema){
        int points=0;
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();
        Die tempDie;

        while(diceIterator.hasNext()){
            diceIterator.next();
            tempDie=schema.getCell(diceIterator.getRow(),diceIterator.getColumn()).getDie();
            if(color.toString().equals(tempDie.toString())){
                points+=tempDie.getShade().toInt();
            }
        }
        return points;
    }
}