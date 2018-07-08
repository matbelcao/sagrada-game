package it.polimi.ingsw.common.serializables;

/**
 * This enum represents the possible updates event that can occur during a match.
 */
public enum GameEvent {
    ROUND_START,
    ROUND_END,
    TURN_START,
    TURN_END,
    RECONNECT,
    DISCONNECT,
    BOARD_CHANGED, QUIT
}
