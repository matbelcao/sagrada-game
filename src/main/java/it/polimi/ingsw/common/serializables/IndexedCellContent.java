package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.server.model.Die;

import java.io.Serializable;

public class IndexedCellContent implements Serializable {
    private Place place;
    private int position;
    private CellContent content;


    public IndexedCellContent(int position, Place place, Shade shade, Color color){
        this.content=new LightDie(shade,color);
        this.place=place;
        this.position=position;
    }

    public IndexedCellContent(int position,Place place, Die die){
        this.content=new LightDie(die.getShade(),die.getColor());
        this.place=place;
        this.position=position;
    }

    public IndexedCellContent(int position,String place, String face, String color){
        this.content=new LightDie(face,color);
        this.place=Place.valueOf(place.toUpperCase());
        this.position=position;
    }

    public IndexedCellContent(int position,String place, String constraint){
        this.content=new LightConstraint(constraint);
        this.place=Place.valueOf(place);
        this.position=position;
    }
    public IndexedCellContent(int position,Place place, Shade shade){
        this.content=new LightConstraint(shade);
        this.place=place;
        this.position=position;
    }
    public IndexedCellContent(int position,Place place, Color color){
        this.content=new LightConstraint(color);
        this.place=place;
        this.position=position;
    }

    public int getPosition(){
        return position;
    }

    public Place getPlace() {
        return place;
    }

    public CellContent getContent() {
        return content;
    }

}
