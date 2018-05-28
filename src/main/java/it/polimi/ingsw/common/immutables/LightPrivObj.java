package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.server.model.PrivObjectiveCard;

public class LightPrivObj extends LightCard {
    private Color color;
    public LightPrivObj(String name, String description, String imgSrc, int id, Color color) {
        super(name, description, imgSrc, id);
        this.color = color;
    }

    public static LightPrivObj toLightPrivObj(PrivObjectiveCard priv){
        return new LightPrivObj(priv.getName(),priv.getDescription(),priv.getImgSrc(),priv.getId(),priv.getColor());
    }

    public Color getColor() {
        return color;
    }
}
