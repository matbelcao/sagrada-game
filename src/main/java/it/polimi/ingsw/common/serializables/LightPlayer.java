package it.polimi.ingsw.common.serializables;

import java.io.Serializable;

/**
 * This class is a lighter, serializable representation of a Player
 */
public class LightPlayer implements Serializable {
    private String username;
    private Integer playerId;
    private Integer points;
    private Integer finalPosition;
    private LightSchemaCard schema;
    private int favorTokens;
    private LightPlayerStatus status;

    /**
     * The LightPlayer constructor
     * @param username the player's username
     * @param playerId the player's ID
     */
    public LightPlayer(String username, int playerId) {
        this.username = username;
        assert (playerId < 4 && playerId >= 0);
        this.playerId = playerId;
        this.points=0;
        this.finalPosition=0;
        this.status=LightPlayerStatus.PLAYING;
    }

    public LightPlayerStatus getStatus(){
        return status;
    }

    public int getFavorTokens() {
        return favorTokens;
    }

    public void setFavorTokens(int favorTokens) {
        this.favorTokens = favorTokens;
    }

    public String getUsername() {
        return username;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setSchema(LightSchemaCard schema) {
        this.schema = schema;
    }

    public void setFinalPosition(int position){this.finalPosition=position;}

    public Integer getFinalPosition(){return finalPosition;}

    public LightSchemaCard getSchema() {
        return schema;
    }

    public boolean isPlaying() {
        return this.status.equals(LightPlayerStatus.PLAYING);
    }

    public void setStatus(LightPlayerStatus status) {
        this.status=status;
    }


}

