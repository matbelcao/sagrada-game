package it.polimi.ingsw;

import java.io.File;
import java.util.Random;

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
     * Constructs the object setting its shade and color
     * @param shade the Face of the die
     * @param color the Color of the die
     */
    Die(int shade, String color ){
        try {
            this.shade = Face.valueOf(shade);
        } catch (IllegalShadeException e) {
            e.printStackTrace();
        }
        this.color = Color.valueOf(color); //make sure color is all CAPS
    }

    /**
     * Gets the Color of the die
     * @return  the Color of the die
     */
    public Color getColor(){
        return this.color;
    }

    /**
     * Gets the Shade of the die
     * @return  the Face of the die
     */
    public Face getShade(){
        return this.shade;
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
     * Rerolls a die
     * @param die the die to be rerolled
     */
    public void reroll(Die die) throws IllegalShadeException {
        Random randomGen = new Random();
        die.setShade(randomGen.nextInt(6)+1);
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
