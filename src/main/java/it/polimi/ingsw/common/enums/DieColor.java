package it.polimi.ingsw.common.enums;


/**
 * This enum offers the set of colors (RED, GREEN, YELLOW, BLUE, PURPLE) needed to build the dice,
 * it also contains the ansi code to apply color to text in the CLI
 */
public enum Color { //need to add png address combined with Shade's ones
    RED("\u001B[91m" ),
    GREEN("\u001B[92m"),
    YELLOW("\u001B[93m"),
    BLUE("\u001B[94m"),
    PURPLE("\u001B[95m"),
    NONE ("\u001B[0m"); //to be used (also) in shade restrictions

    private final String utf;

    /**
     * Constructs the elements of the enum setting the corresponding ansi color code
     * @param utf the Color utf code for CLI
     */
    Color(String utf){
        this.utf=utf;
    }

    /**
     * Gets the ansi color code
     * @return the Color ansi code for CLI
     */
    public String getUtf(){
        return utf;
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

    public static javafx.scene.paint.Color toFXConstraintColor(Color color){
        if(color.equals(RED)){
            return javafx.scene.paint.Color.web("#bb331a");
        }else if(color.equals(GREEN)){
            return javafx.scene.paint.Color.web("#579b55");
        }else if(color.equals(YELLOW)){
            return javafx.scene.paint.Color.web("#e3d107");
        }else if(color.equals(BLUE)){
            return javafx.scene.paint.Color.web("#5faab9");
        }else if(color.equals(PURPLE)){
            return javafx.scene.paint.Color.web("#a5468c");
        }else{
            return javafx.scene.paint.Color.web("#9e9e9e");
        }
    }

    public static javafx.scene.paint.Color toFXColor(Color color){
        if(color.equals(RED)){
            return javafx.scene.paint.Color.RED;
        }else if(color.equals(GREEN)){
            return javafx.scene.paint.Color.GREEN;
        }else if(color.equals(YELLOW)){
            return javafx.scene.paint.Color.YELLOW;
        }else if(color.equals(BLUE)){
            return javafx.scene.paint.Color.BLUE;
        }else
            return javafx.scene.paint.Color.PURPLE;
    }
}
