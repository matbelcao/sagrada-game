package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.server.model.Card;

import java.io.File;
import java.io.Serializable;

public class LightCard implements Serializable {
    private String name;
    private String description;
    private static final String imgSrc="img/PubObjectiveCard/";
    private int id;

    public LightCard(String name, String description, int id){
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public static LightCard toLightCard(Card objective){
        return new LightCard(objective.getName(),objective.getDescription(),objective.getId());
    }

    public static LightCard toLightCard(String objective){
        String [] param= objective.trim().split("\\s+");
        return new LightCard(param[3].replaceAll("_", " "),param[4].replaceAll("_", " "),Integer.parseInt(param[2]));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public  String getImgSrc() {
        return imgSrc+id;
    }

    public int getId() {
        return id;
    }



    public LightCard clone(){
        return new LightCard(this.getName(),this.getDescription(),this.getId());
    }
}
