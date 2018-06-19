package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.server.model.Player;

import java.io.Serializable;

public class LightPlayer implements Serializable {
    private String username;
    private int playerId;
    private int points;
    private int finalPosition;
    private LightSchemaCard schema;
    private int favorTokens;
    private LightPlayerStatus status;

    public LightPlayer(String username, int playerId) {
        this.username = username;
        assert (playerId < 4 && playerId >= 0);
        this.playerId = playerId;
        this.points=0;
        this.finalPosition=0;
        this.status=LightPlayerStatus.PLAYING;
    }

    public static LightPlayer toLightPlayer(Player player){
        String username = player.getUsername();
        int playerId = player.getGameId();
        return new LightPlayer(username,playerId);
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

    public int getFinalPosition(){return finalPosition;}

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

