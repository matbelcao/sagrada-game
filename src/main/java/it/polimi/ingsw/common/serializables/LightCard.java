package it.polimi.ingsw.common.serializables;

import java.io.Serializable;

/**
 * This class is a lighter, serializable representation of a Public Objective Card
 */
public class LightCard implements Serializable {
    private String name;
    private String description;
    private static final String IMG_SRC ="img/PubObjectiveCard/";
    private int id;

    public LightCard(String name, String description, int id){
        this.name = name;
        this.description = description;
        this.id = id;
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

    public  String getImgSrc(){
        return IMG_SRC +id;
    }

    public int getId() {
        return id;
    }



    public LightCard copy(){
        return new LightCard(this.getName(),this.getDescription(),this.getId());
    }
}
