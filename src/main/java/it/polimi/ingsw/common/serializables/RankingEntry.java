package it.polimi.ingsw.common.serializables;

public class RankingEntry {
    private int playerId;
    private int points;
    private int finalPosition;

    /**
     * sets the parameters
     * @param playerId the player's id
     * @param points the points he/she/it obtained
     * @param finalPosition his/her/its final position in the ranking
     */
    public RankingEntry(int playerId, int points, int finalPosition) {
        this.playerId = playerId;
        this.points = points;
        this.finalPosition = finalPosition;
    }

    /**
     * @return the player's id
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * @return the points
     */
    public int getPoints() {
        return points;
    }

    /**
     * @return the final position
     */
    public int getFinalPosition() {
        return finalPosition;
    }
}
