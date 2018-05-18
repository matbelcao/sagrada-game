package it.polimi.ingsw.server.model.immutables;
import it.polimi.ingsw.server.model.SchemaCard;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class is a lighter, more efficient but immutable representation of a schema card
 */
public class LightSchemaCard {
    private final String name;
    private int favorTokens;
    private HashMap<Integer,CellContent> cells=new HashMap<>(30);

    /**
     * This is the constructor of the class, it's the only way to set dice and constraints where they belong
     * @param name the name of the schema card
     * @param contentMap a map containing indexes and content(dice or constraints)
     * @param favorTokens the favor tokens associated with the schema
     */
    public LightSchemaCard(String name, Map<Integer,CellContent> contentMap, int favorTokens){
        this.name = name;
        this.favorTokens=favorTokens;
        if(!hasValidKeys(contentMap)){ throw new IllegalArgumentException(); }
        this.cells.putAll(contentMap);
        contentMap.clear();
    }

    /**
     * This method checks if the parameter contentMap is valid
     * @param contentMap the map that needs to be checked
     * @return true iff contentmap is valid
     */
    private boolean hasValidKeys(Map<Integer,CellContent> contentMap) {
        int maxKey=-1;
        int minKey=SchemaCard.NUM_COLS*SchemaCard.NUM_ROWS;
        if(contentMap.isEmpty()){ return false; }
        //compute max and min
        for(Map.Entry<Integer,CellContent> entry : contentMap.entrySet()){
            if (entry.getValue()==null){ return false; }
            if(entry.getKey()<minKey){ minKey=entry.getKey(); }
            if(entry.getKey()>maxKey){ maxKey=entry.getKey(); }
        }

        return (maxKey < SchemaCard.NUM_COLS*SchemaCard.NUM_ROWS && minKey >= 0);
    }

    /**
     * @return the name of the schema card
     */
    public String getName() { return name; }

    /**
     * @return the number of favor tokens associated with the card
     */
    public int getFavorTokens(){ return this.favorTokens; }

    /**
     * Tells whether or not the schema has a die in the position indicated by the @param
     * @param index the position (0 to 19)
     * @return true iff the schema has a die in the position indicated by the @param
     */
    public boolean hasDieAt(int index){
        if(this.cells.containsKey(index)){
            return this.cells.get(index).isDie();
        }
        return false;
    }

    /**
     * Tells whether or not the schema has a die in the position indicated by the @param
     * @param row the row in the schema
     * @param column the column in the schema
     * @return true iff the schema has a die in the position indicated by the @param
     */
    public boolean hasDieAt(int row, int column){
        int index= row * SchemaCard.NUM_COLS + column;
        return hasDieAt(index);
    }

    /**
     * Tells whether or not the schema has a constraint in the position indicated by the @param
     * @param index the position (0 to 19)
     * @return true iff the schema has a constraint in the position indicated by the @param
     */
    public boolean hasConstraintAt(int index){
        if(this.cells.containsKey(index)) {
            return !this.cells.get(index).isDie();
        }
        return false;
    }

    /**
     * Tells whether or not the schema has a constraint in the position indicated by the @param
     * @param row the row in the schema
     * @param column the column in the schema
     * @return true iff the schema has a constraint in the position indicated by the @param
     */
    public boolean hasConstraintAt(int row, int column){
        int index=row * SchemaCard.NUM_COLS + column;
        return hasConstraintAt(index);
    }

    /**
     * Returns the die positioned at said index
     * @param index the position where to get the die from
     * @return the die in position index if there is one
     */
    public LightDie getDieAt(int index){
        if(!hasDieAt(index)){ throw new NoSuchElementException(); }
        return (LightDie) this.cells.get(index);
    }

    /**
     * Returns the die positioned at (row, column ) in the schema
     * @param row the row of the schema
     * @param column the column of the schema
     * @return the die placed there iff there is one
     */
    public LightDie getDieAt(int row,int column){
        int index=row * SchemaCard.NUM_COLS + column;
        return getDieAt(index);
    }

    /**
     * Returns the constraint positioned at said index
     * @param index the position where to get the constraint from
     * @return the constraint in position index if there is one
     */
    public LightConstraint getConstraintAt(int index){
        if(!hasConstraintAt(index)){ throw new NoSuchElementException(); }
        return (LightConstraint) this.cells.get(index);
    }

    /**
     * Returns the constraint positioned at (row, column ) in the schema
     * @param row the row of the schema
     * @param column the column of the schema
     * @return the constraint placed there iff there is one
     */
    public LightConstraint getConstraintAt(int row,int column){
        int index=row * SchemaCard.NUM_COLS + column;
        return getConstraintAt(index);
    }


}
