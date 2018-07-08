package it.polimi.ingsw.common.enums;

/**
 * Enum class tha represents the die quantity (for toolcard usage)
 */
public enum DieQuantity {
    ONE,
    TWO,
    ALL,
    NONE;

    public static DieQuantity toDieQuantity(String qty) {
        switch (qty) {
            case "one":
                return ONE;
            case "two":
                return TWO;
            case "all":
                return ALL;
            default:
                return NONE;
        }
    }

    public static DieQuantity toDieQuantity(int qty) {
        switch (qty) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            default:
                return NONE;
        }
    }
}

