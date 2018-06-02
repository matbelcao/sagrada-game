package it.polimi.ingsw.common.enums;

public enum DieQuantity {
    ONE,
    TWO,
    ALL,
    ONE_TWO;

    public static DieQuantity toDieQuantity(String qty) {
        switch (qty) {
            case "one":
                return ONE;
            case "two":
                return TWO;
            case "all":
                return ALL;
            case "one_two":
                return ONE_TWO;
            default:
                return null;
        }
    }
}

