package it.polimi.ingsw.common.enums;


/**
 * This enum represents the possible faces (shades) of the game dice. The elements are immutable.
 */
public enum Shade { // need to add address for png of the face
    ONE(1,"\u2680"),
    TWO(2,"\u2681"),
    THREE(3,"\u2682"),
    FOUR(4,"\u2683"),
    FIVE(5,"\u2684"),
    SIX(6,"\u2685");

    private final int shadeDie;
    private final String utf;
    public static final String EMPTY = "\u25a0"; //to be used in color restrictions

    /**
     * Constructs the elements of the enum
     * @param shadeDie the shade of the die
     * @param utf the utf code that renders the corresponding die face
     */
    Shade(final int shadeDie, final String utf){
        this.shadeDie=shadeDie;
        this.utf=utf;
    }

    /**
     * Gets a die face through its int value instead of its string name
     * @param shade the number corresponding to the wanted face of the die
     * @return the instance of Shade corresponding to the number
     */

    public static Shade valueOf(int shade) {

        for (Shade face : Shade.values()) {
            if (face.ordinal() == (shade - 1)) {
                return face;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Gets the integer value of the face of the die
     * @return the integer number between 1 and 6 corresponding to the Shade
     */

    public int toInt(){
        return this.shadeDie;
    }

    /**
     * Gets the utf code for the CLI representation of the die face
     * @return the string containing the utf java code for the CLI
     */
    public String getUtf(){
        return this.utf;
    }


    /**
     * Checks whether a string is a valid Shade name
     * @param shade the string to be checked
     * @return true iff the string equals the name of one of the listed shades
     */
    public static Boolean contains(String shade) {
        for (Shade c : Shade.values()) {
            if (c.toString().equals(shade)) {
                return true;
            }
        }
        return false;
    }
}
