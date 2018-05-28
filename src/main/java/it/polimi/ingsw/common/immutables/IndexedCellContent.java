package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Face;
import it.polimi.ingsw.server.model.Die;

import java.util.ArrayList;
import java.util.List;

public class IndexedCellContent {
    private CellContent content;
    private int index;

    public IndexedCellContent(int index, Face face, Color color){
        this.content=new LightDie(face,color);
        this.index=index;
    }
    public IndexedCellContent(int index, String face, String color){
        this.content=new LightDie(face,color);
        this.index=index;
    }

    public IndexedCellContent(int index, String constraint){
        this.content=new LightConstraint(constraint);
        this.index=index;
    }
    public IndexedCellContent(int index, Face shade){
        this.content=new LightConstraint(shade);
        this.index=index;
    }
    public IndexedCellContent(int index, Color color){
        this.content=new LightConstraint(color);
        this.index=index;
    }

    public int getIndex(){
        return index;
    }

    public CellContent getContent() {
        return content;
    }

    /**
     * builds a list of indexed cellcontents that represents the roundtrack
     * @param roundTrack the actual roundtrack
     * @return the light version of the roundtrack
     */
    public static List<IndexedCellContent> toLightRoundTrack(List<List<Die>> roundTrack){
        ArrayList<IndexedCellContent> result= new ArrayList<>();
        Die die;
        for(int round=0; round<roundTrack.size();round++){
            for(int i=0; i<roundTrack.get(round).size();i++) {
                die=roundTrack.get(round).get(i);
                result.add(new IndexedCellContent(round, die.getShade(), die.getColor()));
            }
        }
        return result;
    }

    /**
     * builds a lis of LightDie from a draftpool
     * @param draftpool the draftpool to be copied
     * @return the light version of the draftpool
     */
    public static List<LightDie> toLightDraftPool(List<Die> draftpool){
        ArrayList<LightDie> result= new ArrayList<>();
        for(int index=0; index<draftpool.size();index++){
            result.add(LightDie.toLightDie(draftpool.get(index)));
        }
        return result;
    }

}
