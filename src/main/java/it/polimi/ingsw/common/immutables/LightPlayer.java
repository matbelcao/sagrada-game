package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.server.model.Player;

public class LightPlayer {
    String username;
    int playerId;
    int points;
    LightSchemaCard schema;

    public LightPlayer(String username, int playerId) {
        this.username = username;
        assert (playerId < 4 && playerId >= 0);
        this.playerId = playerId;
        this.points=0;
    }

    public static LightPlayer toLightPlayer(Player player){
        String username = player.getUsername();
        int playerId = player.getGameId();
        LightPlayer lightPlayer = new LightPlayer(username,playerId);
        lightPlayer.setPoints(player.getScore());
        lightPlayer.setSchema(LightSchemaCard.toLightSchema(player.getSchema()));
        return lightPlayer;
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

    public LightSchemaCard getSchema() {
        return schema;
    }
}

