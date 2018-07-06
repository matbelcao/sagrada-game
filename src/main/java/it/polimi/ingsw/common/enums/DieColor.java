package it.polimi.ingsw.common.enums;
import javafx.scene.paint.Color;

/**
 * This enum offers the set of colors (RED, GREEN, YELLOW, BLUE, PURPLE) needed to build the dice,
 * it also contains the ansi code to apply color to text in the CLI
 */
public enum DieColor { //need to add png address combined with Shade's ones
    RED("\u001B[91m",   Color.web("#bb331a"), Color.web("#ff4848")   ),
    GREEN("\u001B[92m", Color.web("#579b55"), Color.web("#08ad3f")   ),
    YELLOW("\u001B[93m",Color.web("#e3d107"), Color.web("#ffe100")   ),
    BLUE("\u001B[94m",  Color.web("#5faab9"), Color.web("#0086fc")  ),
    PURPLE("\u001B[95m",Color.web("#a5468c"), Color.web("#8226b7")   ),
    NONE ("\u001B[0m",  Color.web("#222222"), Color.web("#333333")   ); //to be used (also) in shade restrictions

    private final String utf;
    private final Color constraintColor;
    private final Color dieColor;

    /**
     * Constructs the elements of the enum setting the corresponding ansi color code
     * @param utf the DieColor utf code for CLI
     * @param constraintColor the java fx color for a constraint
     * @param dieColor the javafx color for a die
     */
    DieColor(String utf, Color constraintColor, Color dieColor){
        this.utf=utf;
        this.constraintColor = constraintColor;
        this.dieColor = dieColor;
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

    public Color getFXConstraintColor(){
        return constraintColor;
    }

    public Color getFXColor(){
        return dieColor;
    }
}
