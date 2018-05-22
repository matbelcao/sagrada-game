package it.polimi.ingsw.common.enums;


/**
 * This enum represents the possible faces (shades) of the game dice. The elements are immutable.
 */
public enum Face { // need to add address for png of the face
    ONE(1,"\u2680"),
    TWO(2,"\u2681"),
    THREE(3,"\u2682"),
    FOUR(4,"\u2683"),
    FIVE(5,"\u2684"),
    SIX(6,"\u2685");

    private final int shade;
    private final String utf;
    public static final String EMPTY = "\u25a0"; //to be used in color restrictions

    /**
     * Constructs the elements of the enum
     * @param shade the shade of the die
     * @param utf the utf code that renders the corresponding die face
     */
    Face(final int shade, final String utf){
        this.shade=shade;
        this.utf=utf;
    }

    /**
     * Gets a die face through its int value instead of its string name
     * @param shade the number corresponding to the wanted face of the die
     * @return the instance of Face corresponding to the number
     */

    public static Face valueOf(int shade) {

        for (Face face : Face.values()) {
            if (face.ordinal() == (shade - 1)) {
                return face;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Gets the integer value of the face of the die
     * @return the integer number between 1 and 6 corresponding to the Face
     */

    public int toInt(){
        return this.shade;
    }

    /**
     * Gets the utf code for the CLI representation of the die face
     * @return the string containing the utf java code for the CLI
     */
    public String getUtf(){
        return this.utf;
    }


    /**
     * Checks whether a string is a valid Face name
     * @param shade the string to be checked
     * @return true iff the string equals the name of one of the listed shades
     */
    public static Boolean contains(String shade) {
        for (Face c : Face.values()) {
            if (c.toString().equals(shade)) {
                return true;
            }
        }
        return false;
    }
}
