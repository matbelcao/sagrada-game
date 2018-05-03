package it.polimi.ingsw.scorecalculator;

import it.polimi.ingsw.Cell;
import it.polimi.ingsw.FullCellIterator;
import it.polimi.ingsw.SchemaCard;

public class ScoreCalculator9 implements ScoreCalculator{

    @Override
    public int calculateScore(SchemaCard schema) {
        int points;
        FullCellIterator fullCell = (FullCellIterator) schema.iterator();
        Cell next;
        while(fullCell.hasNext()){
            next=fullCell.next();


        }

        return 0;
    }
 private boolean checkDiagonalAdjacence(SchemaCard schema,FullCellIterator fullCell){
        //if(schema.getCell(fullCell.getRow(),fullCell.getColumn()).getDie())
     return true;
 }
}
