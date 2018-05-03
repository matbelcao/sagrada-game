package it.polimi.ingsw;


import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class iterates on the cells of a schema card that HAVE A DIE placed on them
 */
public class FullCellIterator implements Iterator <Cell>{
    private Cell[][] cells;
    private Cell next;
    private int index;

    /**
     * Constructs the iterator object
     * @param cells the matrix containing the Cells of the schema card
     */
    FullCellIterator(Cell [][] cells) {
        this.cells=cells;
        this.index=0;
        this.next =null;
    }

    /**
     * Tells if there's another die in the matrix
     * @return true if there's a valid die
     */
    public boolean hasNext() {
        while(index < SchemaCard.NUM_COLS * SchemaCard.NUM_ROWS){
            if(this.cells[index/SchemaCard.NUM_COLS][index%SchemaCard.NUM_COLS].hasDie()){
                next=this.cells[index/SchemaCard.NUM_COLS][index%SchemaCard.NUM_COLS];
                return true;
            }
            index++;
        }
        next=null;
        return false;
    }

    /**
     * @return the next valid die
     */
    public Cell next() {
        if(this.hasNext()){
            index++;
            return next;
        }
        throw new NoSuchElementException();
    }

    /**
     * Calculates the row of the cell that next is pointing to
     * @return the row of the cell
     */
    public int getRow(){
        return (index-1)/SchemaCard.NUM_COLS;
    }

    /**
     * Calculates the column of the cell that next is pointing to
     * @return the column of the cell
     */
    public int getColumn(){
        return (index-1)%SchemaCard.NUM_COLS;
    }

    /**
     * not implemented
     */
    @Override
    public void remove(){
        throw new UnsupportedOperationException();
    }
}
