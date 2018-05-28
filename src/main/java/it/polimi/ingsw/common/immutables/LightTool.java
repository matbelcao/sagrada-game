package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.server.model.ToolCard;

public class LightTool extends LightCard {
    private boolean used;
    public LightTool(String name, String description, String imgSrc, int id, boolean used) {
        super(name, description, id);
        this.used=used;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public static LightTool toLightTool(ToolCard tool){
        return new LightTool(tool.getName(),tool.getDescription(),tool.getImgSrc(),tool.getId(),tool.hasAlreadyUsed());
    }

}
