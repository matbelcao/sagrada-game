package it.polimi.ingsw.server.model.iterators;


import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.SchemaCard;

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
    public FullCellIterator(Cell[][] cells) {
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
     * returns the number of full cells (that contain a die) in the schemaCard
     * @return the number of dice in the schema card
     */
    public int numOfDice(){
        int numOfDice=0;

        //saving th state of the iterator
        int tempindex=this.index;
        Cell tempnext= this.next;
        //reinitializing the iterator
        this.index=0;
        this.next =null;
        while(hasNext()){
            next();
            numOfDice++;
        }
        //restore original state
        this.index=tempindex;
        this.next=tempnext;
        return numOfDice;
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
     * @return the index (0 to 19) of the cell next is pointing to
     */
    public int getIndex(){
        return index-1;
    }

    /**
     * not implemented
     */
    @Override
    public void remove(){
        throw new UnsupportedOperationException();
    }
}
