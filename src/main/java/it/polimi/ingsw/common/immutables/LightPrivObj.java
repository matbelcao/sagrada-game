package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.server.model.PrivObjectiveCard;

public class LightPrivObj extends LightCard {
    private Color color;
    public LightPrivObj(String name, String description, int id, Color color) {
        super(name, description, id);
        this.color = color;
    }

    public static LightPrivObj toLightPrivObj(PrivObjectiveCard priv){
        return new LightPrivObj(priv.getName(),priv.getDescription(),priv.getId(),priv.getColor());
    }

    public Color getColor() {
        return color;
    }
}
