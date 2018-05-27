package it.polimi.ingsw.common.immutables;

public class LightTool extends LightCard {
    private boolean used;
    public LightTool(String name, String description, String imgSrc, int id, boolean used) {
        super(name, description, imgSrc, id);
        this.used=used;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
