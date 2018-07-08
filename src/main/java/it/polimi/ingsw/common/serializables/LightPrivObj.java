package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.DieColor;

import java.io.Serializable;

public class LightPrivObj extends LightCard implements Serializable {
    private DieColor dieColor;
    private static final String IMG_SRC="img/PrivObjectiveCard/";

    public LightPrivObj(String name, String description, int id, DieColor dieColor) {
        super(name, description, id);
        this.dieColor = dieColor;
    }

    public static LightPrivObj toLightPrivObj(String objective){
        String [] param= objective.trim().split("\\s+");
        return new LightPrivObj (param[3].replaceAll("_", " "),param[4].replaceAll("_", " "),Integer.parseInt(param[2]),DieColor.valueOf(param[5]));
    }

    @Override
    public String getImgSrc() {
        return IMG_SRC+getId();
    }

    public DieColor getDieColor() {
        return dieColor;
    }

}
