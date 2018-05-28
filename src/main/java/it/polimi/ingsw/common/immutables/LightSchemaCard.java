package it.polimi.ingsw.common.immutables;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Constraint;
import it.polimi.ingsw.server.model.SchemaCard;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class is a lighter, more efficient but immutable representation of a schema card
 */
public class LightSchemaCard {
    private final String name;
    private HashMap<Integer,CellContent> cells=new HashMap<>(30);

    /**
     * This is the constructor of the class, it's the only way to set dice and constraints where they belong
     * @param name the name of the schema card
     * @param contentMap a map containing indexes and content(dice or constraints)
     */
    public LightSchemaCard(String name, Map<Integer,CellContent> contentMap){
        this.name = name;
        if(!hasValidKeys(contentMap)){ throw new IllegalArgumentException(); }
        this.cells.putAll(contentMap);
        contentMap.clear();
    }

    /**
     * Returns the light version of the given schema card
     * @param schemaCard the schema card to be used as a template for the new light schema
     * @return the newly created LightSchema
     */
    public static  LightSchemaCard toLightSchema(SchemaCard schemaCard){
        HashMap<Integer,CellContent> contentMap = new HashMap<>(30);
        for(int i=0; i<SchemaCard.NUM_COLS*SchemaCard.NUM_ROWS; i++){
            Cell cell = schemaCard.getCell(i);
            if(cell.hasDie()){
                contentMap.put(i,new LightDie(cell.getDie().getShade(),cell.getDie().getColor()));
            }else if(cell.hasConstraint()){
                Constraint constraint = cell.getConstraint();
                if(constraint.isColorConstraint()){
                    contentMap.put(i,new LightConstraint(constraint.getColor()));
                } else {
                    contentMap.put(i, new LightConstraint(constraint.getShade()));
                }
            }
        }
        return new LightSchemaCard(schemaCard.getName(),contentMap);
    }


    public static  LightSchemaCard toLightSchema(String schemaCard){
        String [] parsed= schemaCard.trim().split("\\s+");
        Map<Integer,CellContent> map=new HashMap<>();

        for(int i=3;i<parsed.length;i++){
            String [] cellcontent=parsed[i].trim().split(",");
            int index= (Integer.parseInt(cellcontent[1]) * SchemaCard.NUM_COLS) + Integer.parseInt(cellcontent[2]);
            if(cellcontent[0].equals("D")){
                map.put(index,new LightDie(cellcontent[4],cellcontent[3]));
            }else{
                map.put(index,new LightConstraint(cellcontent[3]));
            }
        }

        return new LightSchemaCard(parsed[2],map);
    }

    /**
     * @return a copy of the map of the content of the schema
     */
    public Map<Integer,CellContent> getCellsMap(){
        return (Map<Integer, CellContent>) cells.clone();
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
