package it.polimi.ingsw.server.model.immutables;

import it.polimi.ingsw.server.model.enums.Color;
import it.polimi.ingsw.server.model.enums.Face;

/**
 * This class offers a "lighter" version of the Die by limiting the interaction with the die to the Getters only
 */
public class LightDie implements CellContent {
    private Color color;
    private Face shade;

    /**
     * Constructs the object setting its shade and color
     * @param shade the Face of the die
     * @param color the Color of the die
     */
    public LightDie(Face shade, Color color){
        this.shade= shade;
        this.color=color;
    }

    /**
     * Constructs the object setting its shade and color
     * @param shade the Face of the die
     * @param color the Color of the die
     */
    public LightDie(String shade, String color){
        this.shade = Face.valueOf(shade);
        this.color = Color.valueOf(color);
    }

    /**
     * Returns the shade of the lightDie
     * @return the shade
     */
    public Face getShade(){ return this.shade; }

    /**
     * Returns the color of the lightDie
     * @return the color
     */
    public Color getColor(){ return this.color; }


    @Override
    public boolean isDie() {
        return true;
    }

    @Override
    public boolean hasColor() {
        return true;
    }
}
