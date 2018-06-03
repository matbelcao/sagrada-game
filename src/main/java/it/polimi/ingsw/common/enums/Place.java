package it.polimi.ingsw.common.enums;

public enum Place {
    DRAFTPOOL,
    SCHEMA,
    ROUNDTRACK,
    DICEBAG,
    NONE;


    public static Place toPlace(String place){
        switch(place){
            case "draftpool":
                return DRAFTPOOL;
            case "schema":
                return SCHEMA;
            case "roundtrack":
                return ROUNDTRACK;
            case "dicebag":
                return DICEBAG;
            default:
                return NONE;
        }
    }
}


