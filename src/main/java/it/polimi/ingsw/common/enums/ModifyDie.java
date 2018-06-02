package it.polimi.ingsw.common.enums;

public enum ModifyDie {
    INCREASE_DECREASE,
    SWAP,
    REROLL,
    FLIP,
    SETSHADE;

    public static ModifyDie toModifyDie(String modify){
        switch(modify){
            case "increase_decrease":
                return INCREASE_DECREASE;
            case "swap":
                return SWAP;
            case "reroll":
                return REROLL;
            case "flip":
                return FLIP;
            case "setshade":
                return SETSHADE;
            default:
                return null;
        }
    }

}
