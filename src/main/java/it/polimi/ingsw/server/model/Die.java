package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.server.model.exceptions.IllegalShadeException;
import it.polimi.ingsw.common.serializables.LightDie;

import java.io.File;
import java.util.Random;

/**
 * This class represents one Die of the game with its color and shade (face). The class is immutable
 */
public class Die {
    private LightDie lightDie;

    private static final int MAX_SHADE=6;
    private static final int MIN_SHADE=1;


    /**
     * Constructs the object setting its shade and dieColor
     * @param shade the Shade of the die
     * @param dieColor the DieColor of the die
     */
    public Die(Shade shade, DieColor dieColor){
        this.lightDie =new LightDie(shade, dieColor);
    }

    /**
     * Constructs the object setting its shade and color
     * @param shade the Shade of the die
     * @param color the DieColor of the die
     */
    public Die(String shade, String color){
        this.lightDie =new LightDie(shade,color);
    }

    /**
     * Constructs the object setting its shade and color
     * @param shade the Shade of the die
     * @param color the DieColor of the die
     */
    public Die(int shade, String color ){
        this.lightDie =new LightDie(Shade.valueOf(shade),DieColor.valueOf(color));
    }

    /**
     * Gets the DieColor of the die
     * @return  the DieColor of the die
     */
    public DieColor getColor(){
        return this.lightDie.getDieColor();
    }

    /**
     * Gets the Shade of the die
     * @return  the Shade of the die
     */
    public Shade getShade(){
        return this.lightDie.getShade();
    }

    /**
     * Increases by one the shade of the die
     */
    public void increaseShade () throws IllegalShadeException{
        if(this.lightDie.getShade().toInt()==MAX_SHADE){
            throw new IllegalShadeException();
        }
        this.setShade(this.lightDie.getShade().toInt() + 1);
    }

    /**
     * Decreases by one the shade of the die
     * @throws IllegalShadeException if the shade is equal to ONE
     */
    public void decreaseShade () throws IllegalShadeException{
        if(this.lightDie.getShade().toInt()==MIN_SHADE){
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
        this.setShade(randomGen.nextInt(MAX_SHADE)+1);
    }

    public void swap(Die die){
        String tmpColor = this.getColor().toString();
        int tmpFace = this.getShade().toInt();
        this.lightDie=new LightDie(die.getShade(),die.getColor());
        die.setShade(tmpFace);
        die.setColor(tmpColor);
    }

    /**
     * Sets a new shade to the die
     * @param shade the new shade to be set
     */
    public void setShade(int shade){
        this.lightDie = new LightDie(Shade.valueOf(shade),this.lightDie.getDieColor());
    }

    public void setColor(String color){this.lightDie=new LightDie(this.lightDie.getShade(),DieColor.valueOf(color));}

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
        return this.getColor().getUtf()+this.getShade().getUtf()+DieColor.NONE.getUtf();
    }
}
