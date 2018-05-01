package it.polimi.ingsw;

import org.jetbrains.annotations.Contract;

/**
 * This enum offers the set of colors (RED, GREEN, YELLOW, BLUE, PURPLE) needed to build the dice,
 * it also contains the ansi code to apply color to text in the CLI
 */
public enum Color {
    RED("\u001B[31m" ),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m");

    static final String RESET = "\u001B[0m"; //to be used (also) in shade restrictions

    private final String ansi;

    /**
     * Constructs the elements of the enum setting the corresponding ansi color code
     * @param ansi the Color ansi code for CLI
     */
    Color(String ansi){
        this.ansi=ansi;
    }

    /**
     * Gets the ansi color code
     * @return the Color ansi code for CLI
     */

    public String ansi(){
        return ansi;
    }

    /**
     * Checks whether a string is a valid Color name
     * @param value the string to be checked
     * @return true iff the string equals the name of one of the listed colors
     */
    public static boolean contains(String value) {
        for (Color c : Color.values()) {
            if (c.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
