package it.polimi.ingsw;

import org.jetbrains.annotations.Contract;

public enum Color {
    RED("\u001B[31m" ),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m");

    static final String RESET = "\u001B[0m"; //to be used (also) in shade restrictions


    private String ansi;

    Color(String ansi){
        this.ansi=ansi;
    }

    @Contract(pure = true)
    public String ansi(){
        return ansi;
    }

    public static boolean contains(String value) {

        for (Color c : Color.values()) {
            if (c.toString().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
