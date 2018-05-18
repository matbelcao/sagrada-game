package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.immutables.LightDie;
import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Face;
import it.polimi.ingsw.server.model.exceptions.*;

import java.io.File;
import java.util.Random;
import java.util.logging.FileHandler;

/**
 * This class represents one Die of the game with its color and shade (face). The class is immutable
 */
public class Die {
    private LightDie lightDie;

    /**
     * Constructs the object setting its shade and color
     * @param shade the Face of the die
     * @param color the Color of the die
     */
    public Die(Face shade, Color color){
        this.lightDie =new LightDie(shade,color);
    }

    /**
     * Constructs the object setting its shade and color
     * @param shade the Face of the die
     * @param color the Color of the die
     */
    public Die(String shade, String color){
        this.lightDie =new LightDie(shade,color);
    }

    /**
     * Constructs the object setting its shade and color
     * @param shade the Face of the die
     * @param color the Color of the die
     */
    Die(int shade, String color ){
        this.lightDie =new LightDie(Face.valueOf(shade),Color.valueOf(color));
    }

    /**
     * Gets the Color of the die
     * @return  the Color of the die
     */
    public Color getColor(){
        return this.lightDie.getColor();
    }

    /**
     * Gets the Shade of the die
     * @return  the Face of the die
     */
    public Face getShade(){
        return this.lightDie.getShade();
    }

    /**
     * Increases by one the shade of the die
     */
    public void increaseShade () throws IllegalShadeException{
        if(this.lightDie.getShade().toInt()==6){
            throw new IllegalShadeException();
        }
        this.setShade(this.lightDie.getShade().toInt() + 1);
    }

    /**
     * Decreases by one the shade of the die
     * @throws IllegalShadeException if the shade is equal to ONE
     */
    public void decreaseShade () throws IllegalShadeException{
        if(this.lightDie.getShade().toInt()==1){
            throw new IllegalShadeException();
        }
        this.setShade(this.getShade().toInt() - 1);
    }

    /**
     * Flips die and sets the opposite face's shade
     */
    public void flipShade () {
        this.setShade(7 - this.getShade().toInt());
    }

    /**
     * Rerolls a die
     * @param die the die to be rerolled
     */
    public static void reroll(Die die){
        Random randomGen = new Random();
        die.setShade(randomGen.nextInt(6)+1);
    }

    /**
     * Sets a new shade to the die
     * @param shade the new shade to be set
     */
    public void setShade(int shade){
        this.lightDie = new LightDie(Face.valueOf(shade),this.lightDie.getColor());
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
        return this.getColor().getUtf()+this.getShade().getUtf()+Color.RESET;
    }
}
