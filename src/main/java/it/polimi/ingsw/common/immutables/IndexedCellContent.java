package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Face;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.server.model.Die;

public class IndexedCellContent {
    private Place place;
    private int position;
    private CellContent content;


    public IndexedCellContent(int position,Place place, Face face, Color color){
        this.content=new LightDie(face,color);
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
    public IndexedCellContent(int position,Place place, Face shade){
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
