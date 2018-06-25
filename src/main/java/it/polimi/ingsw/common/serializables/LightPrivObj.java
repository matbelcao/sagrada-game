package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.server.model.PrivObjectiveCard;

import java.io.Serializable;

public class LightPrivObj extends LightCard implements Serializable {
    private Color color;
    private static String imgSrc="img/PrivObjectiveCard/";

    public LightPrivObj(String name, String description, int id, Color color) {
        super(name, description, id);
        this.color = color;
    }

    public static LightPrivObj toLightPrivObj(PrivObjectiveCard priv){
        return new LightPrivObj(priv.getName(),priv.getDescription(),priv.getId(),priv.getColor());
    }

    public static LightPrivObj toLightPrivObj(String objective){
        String [] param= objective.trim().split("\\s+");
        return new LightPrivObj (param[3].replaceAll("_", " "),param[4].replaceAll("_", " "),Integer.parseInt(param[2]),Color.valueOf(param[5]));
    }

    public String getImgSrc() {
        return imgSrc+getId();
    }

    public Color getColor() {
        return color;
    }

}
