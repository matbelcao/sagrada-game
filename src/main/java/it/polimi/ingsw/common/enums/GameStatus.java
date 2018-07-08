package it.polimi.ingsw.common.enums;

/**
 * This enum contains some information about the resources requested to the server. The elements are immutable.
 */
public enum GameStatus {
    INITIALIZING,
    TURN_RUN,
    REQUESTED_SCHEMA_CARD,
    REQUESTED_ROUND_TRACK,
    REQUESTED_DRAFT_POOL;

    public static Place toPlaceFrom(GameStatus status) {
        switch (status) {
            case REQUESTED_SCHEMA_CARD:
                return Place.SCHEMA;
            case REQUESTED_ROUND_TRACK:
                return Place.ROUNDTRACK;
            case REQUESTED_DRAFT_POOL:
                return Place.DRAFTPOOL;
            default:
                return Place.NONE;
        }
    }
}
