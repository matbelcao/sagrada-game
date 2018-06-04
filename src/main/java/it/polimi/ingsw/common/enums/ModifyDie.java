package it.polimi.ingsw.common.enums;

public enum ModifyDie {
    INCREASE,
    DECREASE,
    SWAP,
    REROLL,
    FLIP,
    SETSHADE,
    NONE;

    public static ModifyDie toModifyDie(String modify){
        switch(modify){
            case "increase":
                return INCREASE;
            case "decrease":
                return DECREASE;
            case "swap":
                return SWAP;
            case "reroll":
                return REROLL;
            case "flip":
                return FLIP;
            case "setshade":
                return SETSHADE;
            default:
                return NONE;
        }
    }

}
