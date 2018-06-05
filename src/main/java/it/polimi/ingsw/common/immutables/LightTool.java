package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.server.model.ToolCard;

import java.io.File;

public class LightTool extends LightCard {
    private static  String imgSrc="src"+ File.separator+"img"+File.separator+"ToolCard"+File.separator;
    private boolean used;
    public LightTool(String name, String description, int id, boolean used) {
        super(name, description, id);
        this.used=used;
    }

    public String getImgSrc() {
        return imgSrc+getId();
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public static LightTool toLightTool(ToolCard tool){
        return new LightTool(tool.getName(),tool.getDescription(),tool.getId(),tool.isAlreadyUsed());
    }

    public static LightTool toLightTool(String objective){
        String [] param= objective.trim().split("\\s+");
        return new LightTool (param[3].replaceAll("_", " "),param[4].replaceAll("_", " "),Integer.parseInt(param[2]),Boolean.parseBoolean(param[5]));
    }

    @Override
    public LightTool clone(){
        return new LightTool(this.getName(),this.getDescription(),this.getId(),this.isUsed());

    }
}
