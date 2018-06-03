package it.polimi.ingsw.server.model.enums;

public enum IgnoredConstraint {
    ALL,// color and shade
    NONE,
    COLOR,
    SHADE,
    ADJACENCY,
    FORCE;

    public static IgnoredConstraint toIgnoredConstraint(String constraint){
        switch (constraint){
            case "all":
                return  ALL;
            case "none":
                return NONE;
            case "color":
                return COLOR;
            case "shade":
                return SHADE;
            case "adjacency":
                return ADJACENCY;
            case "force":
                return FORCE;
            default:
                return NONE;
        }
    }
}
