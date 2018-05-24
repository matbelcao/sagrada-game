package it.polimi.ingsw.common.immutables;

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

