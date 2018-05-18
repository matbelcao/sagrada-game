package it.polimi.ingsw.server.model.immutables;

import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Face;

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


}
