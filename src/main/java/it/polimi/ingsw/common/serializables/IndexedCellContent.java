package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.common.enums.Place;

import java.io.Serializable;

/**
 * This class is a lighter, serializable and immutable representation of an Indexed Cell
 */
public class IndexedCellContent implements Serializable {
    private Place place;
    private int position;
    private CellContent content;


    public IndexedCellContent(int position, Place place, Shade shade, DieColor dieColor){
        this.content=new LightDie(shade, dieColor);
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
    public IndexedCellContent(int position,Place place, DieColor dieColor){
        this.content=new LightConstraint(dieColor);
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
