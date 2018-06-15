package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.server.model.Die;

/**
 * This class offers a "lighter" version of the Die by limiting the interaction with the die to the Getters only
 */
public class LightDie implements CellContent {
    private Color color;
    private Shade shade;

    /**
     * Constructs the object setting its shade and color
     * @param shade the Shade of the die
     * @param color the Color of the die
     */
    public LightDie(Shade shade, Color color){
        this.shade= shade;
        this.color=color;
    }

    /**
     * Constructs the object setting its shade and color
     * @param shade the Shade of the die
     * @param color the Color of the die
     */
    public LightDie(String shade, String color){
        this.shade = Shade.valueOf(shade);
        this.color = Color.valueOf(color);
    }

    /**
     * builds a lightdie from a die
     * @param die the die to be copied
     * @return the lightdie
     */
    public static LightDie toLightDie(Die die){
        return new LightDie(die.getShade(),die.getColor());
    }

    /**
     * Returns the shade of the lightDie
     * @return the shade
     */
    public Shade getShade(){ return this.shade; }

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
