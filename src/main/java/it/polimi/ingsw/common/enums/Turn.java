package it.polimi.ingsw.common.enums;

/**
 * This enum represents the turns during the match. The elements are immutable.
 */
public enum Turn {
    FIRST_TURN,
    SECOND_TURN,
    NONE;

    public static Turn toTurn(String when){
        if(when.equals("first_turn")){
            return FIRST_TURN;
        }
        if(when.equals("second_turn")){
            return SECOND_TURN;
        }
        return NONE;
    }
}
