package it.polimi.ingsw.common.enums;

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
