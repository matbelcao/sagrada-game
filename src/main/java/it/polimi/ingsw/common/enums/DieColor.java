package it.polimi.ingsw.common.enums;
import javafx.scene.paint.Color;

/**
 * This enum offers the set of colors (RED, GREEN, YELLOW, BLUE, PURPLE) needed to build the dice,
 * it also contains the ansi code to apply color to text in the CLI
 */
public enum DieColor { //need to add png address combined with Shade's ones
    RED("\u001B[91m" ),
    GREEN("\u001B[92m"),
    YELLOW("\u001B[93m"),
    BLUE("\u001B[94m"),
    PURPLE("\u001B[95m"),
    NONE ("\u001B[0m"); //to be used (also) in shade restrictions

    private final String utf;

    /**
     * Constructs the elements of the enum setting the corresponding ansi color code
     * @param utf the DieColor utf code for CLI
     */
    DieColor(String utf){
        this.utf=utf;
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

    public static Color toFXConstraintColor(DieColor dieColor){
        if(dieColor.equals(RED)){
            return Color.web("#bb331a");
        }else if(dieColor.equals(GREEN)){
            return Color.web("#579b55");
        }else if(dieColor.equals(YELLOW)){
            return Color.web("#e3d107");
        }else if(dieColor.equals(BLUE)){
            return Color.web("#5faab9");
        }else if(dieColor.equals(PURPLE)){
            return Color.web("#a5468c");
        }else{
            return Color.web("#9e9e9e");
        }
    }

    public static Color toFXColor(DieColor dieColor){
        if(dieColor.equals(RED)){
            return Color.RED;
        }else if(dieColor.equals(GREEN)){
            return Color.GREEN;
        }else if(dieColor.equals(YELLOW)){
            return Color.YELLOW;
        }else if(dieColor.equals(BLUE)){
            return Color.BLUE;
        }else
            return Color.PURPLE;
    }
}
