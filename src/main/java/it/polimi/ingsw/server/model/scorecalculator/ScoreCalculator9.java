package it.polimi.ingsw.server.model.scorecalculator;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
import it.polimi.ingsw.server.model.SchemaCard;

/**
 * This class implements the "Color Diagonals" Public Objective Card
 */
public class ScoreCalculator9 implements ScoreCalculator{
    /**
     * Computes the score of the Schema Card that is given by the Public Objective
     * @param schema the schema whom score needs to be calculated
     * @return the score
     */
    @Override
    public int calculateScore(SchemaCard schema) {
        int points=0;
        FullCellIterator fullCell = (FullCellIterator) schema.iterator();
        Cell next;
        while(fullCell.hasNext()){
            next=fullCell.next();
            if(this.checkDiagonalAdjacency(schema,fullCell,next)){ points+=1;}

        }
        return points ;
    }

    /**
     * This method checks whether a cell has some other cell that touches her with just a corner and has a die with the same color of his
     * @param schema the schema to work on
     * @param fullCell the iterator on the schema
     * @param cell the cell containing a placed die
     * @return true iff the cell has a cell that is diagonally adjacent to her and contains a die with the same color
     */
    private boolean checkDiagonalAdjacency(SchemaCard schema,FullCellIterator fullCell, Cell cell){
        Integer row= fullCell.getRow();
        Integer column=fullCell.getColumn();

        if(row > 0 && column > 0
             && schema.getCell(row - 1,column - 1).hasDie()
             && schema.getCell(row - 1,column - 1).getDie().getColor().equals(cell.getDie().getColor())) {
            return true;
        }

        if(row > 0 && column < (SchemaCard.NUM_COLS - 1)
             && schema.getCell(row - 1,column + 1).hasDie()
             && schema.getCell(row - 1,column + 1).getDie().getColor().equals(cell.getDie().getColor())) {
            return true;
        }
        if(row < (SchemaCard.NUM_ROWS - 1) && column > 0
             && schema.getCell(row + 1,column - 1).hasDie()
             && schema.getCell(row + 1,column - 1).getDie().getColor().equals(cell.getDie().getColor())) {
            return true;
        }
        if(row < (SchemaCard.NUM_ROWS - 1) && column < (SchemaCard.NUM_COLS - 1)
             && schema.getCell(row + 1,column + 1).hasDie()
             && schema.getCell(row + 1,column + 1).getDie().getColor().equals(cell.getDie().getColor())) {
            return true;
        }
        return false;

    }
}
