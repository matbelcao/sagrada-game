package it.polimi.ingsw;

import java.io.File;

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
     * Gets the string name of the color of the die
     * @return a String tha is the name of the color of the die
     */
    public String getColor(){
        return this.color!=null? this.color.toString() : null;
    }

    /**
     * Gets the string name of the shade of the die
     * @return a String that is the name of the shade of the die
     */
    public String getShade(){
        return this.shade!=null? this.shade.toString() : null;
    }

    /**
     * Increases by one the shade of the die
     * @throws IllegalShadeException if the shade is equal to SIX
     */
    public void increaseShade () throws IllegalShadeException{
        if(this.shade.toInt()==6){
            throw new IllegalShadeException();
        }
        this.shade = Face.valueOf(this.shade.toInt() + 1);
    }

    /**
     * Decreases by one the shade of the die
     * @throws IllegalShadeException if the shade is equal to ONE
     */
    public void decreaseShade () throws IllegalShadeException{
        if(this.shade.toInt()==1){
            throw new IllegalShadeException();
        }
        this.shade = Face.valueOf(this.shade.toInt() - 1);
    }

    /**
     * Flips die and sets the opposite face's shade
     */
    public void flipShade (){
        try {
            this.shade = Face.valueOf(7 - this.shade.toInt());
        } catch (IllegalShadeException e) { // this can never happen
            e.printStackTrace();
        }
    }

    /**
     * Sets a new shade to the die
     * @param shade the new shade to be set
     */
    public void setShade(int shade) throws IllegalShadeException{
        this.shade = Face.valueOf(shade);
    }

    /**
     * Returns a string representation of the die
     * @return said string
     */
    public String toString(){ return getColor() + File.separator + getShade(); }

    /**
     * Creates a String that renders a correctly colored die in the CLI using UTF-16 DIE FACES
     * @return said string
     */
    public String toUtf(){
        return this.color.ansi()+this.shade.getUtf()+Color.RESET;
    }
}
