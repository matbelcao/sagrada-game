package it.polimi.ingsw.common.enums;
/**
 * This enum offers the set of colors (RED, GREEN, YELLOW, BLUE, PURPLE) needed to build the dice,
 * it also contains the ansi code to apply color to text in the CLI
 */
public enum DieColor { //need to add png address combined with Shade's ones
    RED("\u001B[91m",   "#bb331a", "#ff433f"   ),
    GREEN("\u001B[92m", "#579b55", "#08ad3f"   ),
    YELLOW("\u001B[93m","#e3d107", "#ffe100"   ),
    BLUE("\u001B[94m",  "#5faab9", "#0086fc"  ),
    PURPLE("\u001B[95m","#a5468c", "#8226b7"   ),
    NONE ("\u001B[0m",  "#222222", "#222222"   ); //to be used (also) in shade restrictions

    private final String utf;
    private final String constraintColor;
    private final String colorDie;

    /**
     * Constructs the elements of the enum setting the corresponding ansi color code
     * @param utf the DieColor utf code for CLI
     * @param constraintColor the java fx color for a constraint
     * @param colorDie the javafx color for a die
     */
    DieColor(String utf, String constraintColor, String colorDie){
        this.utf=utf;
        this.constraintColor = constraintColor;
        this.colorDie = colorDie;
    }

    /**
     * Gets the ansi color code
     * @return the DieColor ansi code for CLI
     */
    public String getUtf(){
        return utf;
    }



    /**
     * Checks whether a string is a valid DieColor name
     * @param value the string to be checked
     * @return true iff the string equals the name of one of the listed colors
     */
    public static boolean contains(String value) {
        for (DieColor c : DieColor.values()) {
            if (c.toString().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public String getFXConstraintColor(){
        return constraintColor;
    }

    public String getFXColor(){
        return colorDie;
    }
}
