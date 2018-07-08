package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SerializableServerUtil {

    private SerializableServerUtil(){
        //not implemented
    }

    public static LightPlayer toLightPlayer(Player player){
        String username = player.getUsername();
        int playerId = player.getGameId();
        return new LightPlayer(username,playerId);
    }

    public static IndexedCellContent toIndexedCellContent(int position, Place place, Die die){
        return new IndexedCellContent(position,place,die.getShade(),die.getColor());
    }

    public static LightCard toLightCard(Card objective){
        return new LightCard(objective.getName(),objective.getDescription(),objective.getId());
    }

    /**
     * builds a lightdie from a die
     * @param die the die to be copied
     * @return the lightdie
     */
    public static LightDie toLightDie(Die die){
        return new LightDie(die.getShade(),die.getColor());
    }

    public static LightPrivObj toLightPrivObj(PrivObjectiveCard priv){
        return new LightPrivObj(priv.getName(),priv.getDescription(),priv.getId(),priv.getDieColor());
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
        return new LightSchemaCard(schemaCard.getName(),contentMap,schemaCard.getFavorTokens());
    }

    public static LightTool toLightTool(ToolCard tool){
        return new LightTool(tool.getName(),tool.getDescription(),tool.getId(),tool.isAlreadyUsed());
    }

    /**
     * Constructs an indexed list of dice starting from a simple list of dice
     * @param dieList the original list of dice
     * @return the new immutable indexed List of dice
     */
    public static List<IndexedCellContent> toIndexedDieList(Place from, List<Die> dieList){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        Die die;

        for (int index=0;index<dieList.size();index++){
            die=dieList.get(index);
            indexedCell=SerializableServerUtil.toIndexedCellContent(index,from,die);
            indexedList.add(indexedCell);
        }
        return indexedList;
    }
}
