package it.polimi.ingsw.common.immutables;

public class LightTool extends LightCard {
    private final int favorTokens;
    private boolean used;
    public LightTool(String name, String description, String imgSrc, int id, int favorTokens) {
        super(name, description, imgSrc, id);
        this.favorTokens = favorTokens;
    }

    public int getFavortokens() {
        return favorTokens;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
