package it.polimi.ingsw.common.serializables;

import java.io.Serializable;

public class LightTool extends LightCard implements Serializable {
    private static final String IMG_SRC = "img/ToolCard/";
    private boolean used;
    public LightTool(String name, String description, int id, boolean used) {
        super(name, description, id);
        this.used=used;
    }

    @Override
    public String getImgSrc() {
        return IMG_SRC+getId();
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public static LightTool toLightTool(String objective){
        String [] param= objective.trim().split("\\s+");
        return new LightTool (param[3].replaceAll("_", " "),param[4].replaceAll("_", " "),Integer.parseInt(param[2]),Boolean.parseBoolean(param[5]));
    }

    @Override
    public LightTool copy(){
        return new LightTool(this.getName(),this.getDescription(),this.getId(),this.isUsed());
    }
}
