package it.polimi.ingsw;

/**
 * This class represents one Die of the game with its color and shade (face). The class is immutable
 */
public class Die {
    private Color color;
    private Face shade;

    /**
     * Constructs the object setting its shade and color
     * @param shade the Face of the die
     * @param color the Color of the die
     */
    Die(String shade, String color ){
        this.shade = Face.valueOf(shade); //make sure value is all CAPS
        this.color = Color.valueOf(color); //make sure color is all CAPS
    }

    /**
     * Get the string name of the color of the die
     * @return a String tha is the name of the color of the die
     */
    public String getColor(){
        return this.color!=null? this.color.toString() : null;
    }

    /**
     * Get the string name of the shade of the die
     * @return a String that is the name of the shade of the die
     */
    public String getShade(){
        return this.shade!=null? this.shade.toString() : null;
    }

    /**
     * Creates a String that renders a correctly colored die in the CLI using UTF-16 DIE FACES
     * @return
     */
    @Override
    public String toString(){
        return this.color.ansi()+this.shade.getUtf()+Color.RESET;
    }
}
