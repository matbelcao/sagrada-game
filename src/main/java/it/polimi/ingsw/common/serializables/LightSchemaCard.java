package it.polimi.ingsw.common.serializables;
import it.polimi.ingsw.common.connection.SocketString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class is a lighter, more efficient but immutable representation of a schema card
 */
public class LightSchemaCard implements Serializable {
    private final String name;
    private Map<Integer,CellContent> cells=new HashMap<>(30);
    private int initialFavorTokens;

    public static final int NUM_COLS=5;
    public static final int NUM_ROWS=4;

    /**
     * This is the constructor of the class, it's the only way to set dice and constraints where they belong
     * @param name the name of the schema card
     * @param contentMap a map containing indexes and content(dice or constraints)
     */
    public LightSchemaCard(String name,int tokens, Map<Integer,CellContent> contentMap){
        this.name = name;
        this.initialFavorTokens=tokens;
        if(!hasValidKeys(contentMap)){ throw new IllegalArgumentException(); }
        this.cells.putAll(contentMap);
        contentMap.clear();
    }

    /**
     * This is the constructor of the class, it's the only way to set dice and constraints where they belong
     * @param name the name of the schema card
     * @param contentMap a map containing indexes and content(dice or constraints)
     * @param initialFavorTokens the number of favor tokens associated with the schema
     */
    public LightSchemaCard(String name, Map<Integer,CellContent> contentMap,int initialFavorTokens){
        this.initialFavorTokens=initialFavorTokens;
        this.name = name;
        if(!hasValidKeys(contentMap)){ throw new IllegalArgumentException(); }
        this.cells.putAll(contentMap);
        contentMap.clear();
    }

    public static  LightSchemaCard toLightSchema(String schemaCard){
        String [] parsed= schemaCard.trim().split("\\s+");
        Map<Integer,CellContent> map=new HashMap<>();
        int favorTokens=Integer.parseInt(parsed[3]);
        for(int i=4;i<parsed.length;i++){
            String [] cellcontent=parsed[i].trim().split(",");
            int index= Integer.parseInt(cellcontent[1]);
            if(cellcontent[0].equals(SocketString.DIE)){
                map.put(index,new LightDie(cellcontent[3],cellcontent[2]));
            }else{
                map.put(index,new LightConstraint(cellcontent[2]));
            }
        }

        return new LightSchemaCard(parsed[2].replaceAll("_"," "),map,favorTokens);
    }

    public int getFavorTokens() {
        return initialFavorTokens;
    }

    /**
     * @return a copy of the map of the content of the schema
     */
    public Map<Integer,CellContent> getCellsMap(){
        Map<Integer,CellContent> copy= new HashMap<>();
        for(Map.Entry<Integer,CellContent> entry: cells.entrySet()){
            copy.put(entry.getKey(),entry.getValue());
        }
        return copy;
    }


    /**
     * This method checks if the parameter contentMap is valid
     * @param contentMap the map that needs to be checked
     * @return true iff contentmap is valid
     */
    private boolean hasValidKeys(Map<Integer,CellContent> contentMap) {
        int maxKey=-1;
        int minKey=NUM_COLS*NUM_ROWS;
        if(contentMap.isEmpty()){ return false; }
        //compute max and min
        for(Map.Entry<Integer,CellContent> entry : contentMap.entrySet()){
            if (entry.getValue()==null){ return false; }
            if(entry.getKey()<minKey){ minKey=entry.getKey(); }
            if(entry.getKey()>maxKey){ maxKey=entry.getKey(); }
        }

        return (maxKey < NUM_COLS*NUM_ROWS && minKey >= 0);
    }

    /**
     * @return the name of the schema card
     */
    public String getName() { return name; }


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
        int index= row * NUM_COLS + column;
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
        int index=row * NUM_COLS + column;
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
        int index=row * NUM_COLS + column;
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
        int index=row * NUM_COLS + column;
        return getConstraintAt(index);
    }


}
