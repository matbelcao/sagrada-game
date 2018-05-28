package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.server.model.Card;

public class LightCard {
    private String name;
    private String description;
    private String imgSrc;
    private int id;

    public LightCard(String name, String description, String imgSrc, int id){
        this.name = name;
        this.description = description;
        this.imgSrc = imgSrc;
        this.id = id;
    }

    public static LightCard toLightCard(Card objective){
        return new LightCard(objective.getName(),objective.getDescription(),objective.getImgSrc(),objective.getId());
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public int getId() {
        return id;
    }
}
