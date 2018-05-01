package it.polimi.ingsw;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * This class is an iterator on dice that have been placed in a given schema card
 */
public class DieIterator implements Iterator {
    private Cell[][] cells;
    private Cell next;
    private int index;

    /**
     * Constructs the iterator object
     * @param cells the matrix containing the Cells of the schema card
     */
    public DieIterator(Cell [][] cells) {
        this.cells=cells;
        this.index=0;
        this.next =null;
    }

    /**
     * Tells if there's another die in the matrix
     * @return true if there's a valid die
     */
    public boolean hasNext() {
        while(index<20){
            if(this.cells[index/5][index%5].getDie()!=null){
                next=this.cells[index/5][index%5];
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
    public Object next() {
        while(this.hasNext()){
            index++;
            return next;
        }
        return null;
    }

    /**
     * Calculates the row of the cell that next is pointing to
     * @return the row of the cell
     */
    public int getRow(){
        return (index-1)/5;
    }

    /**
     * Calculates the column of the cell that next is pointing to
     * @return the column of the cell
     */
    public int getColumn(){
        return (index-1)%5;
    }

    public void remove() {

    }
}