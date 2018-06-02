package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Face;
import it.polimi.ingsw.server.model.exceptions.IllegalShadeException;
import it.polimi.ingsw.common.immutables.LightDie;

import java.io.File;
import java.util.Random;

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
     */
    public void reroll(){
        Random randomGen = new Random();
        this.setShade(randomGen.nextInt(6)+1);
    }

    /**
     * Sets a new shade to the die
     * @param shade the new shade to be set
     */
    public void setShade(int shade){
        this.lightDie = new LightDie(Face.valueOf(shade),this.lightDie.getColor());
    }

    public void setColor(String color){this.lightDie=new LightDie(this.lightDie.getShade(),Color.valueOf(color));}

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
