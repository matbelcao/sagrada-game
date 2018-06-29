package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.server.model.Die;

import java.io.Serializable;

/**
 * This class offers a "lighter" version of the Die by limiting the interaction with the die to the Getters only
 */
public class LightDie implements CellContent,Serializable {
    private DieColor dieColor;
    private Shade shade;

    /**
     * Constructs the object setting its shade and dieColor
     * @param shade the Shade of the die
     * @param dieColor the DieColor of the die
     */
    public LightDie(Shade shade, DieColor dieColor){
        this.shade= shade;
        this.dieColor = dieColor;
    }

    /**
     * Constructs the object setting its shade and dieColor
     * @param shade the Shade of the die
     * @param color the DieColor of the die
     */
    public LightDie(String shade, String color){
        this.shade = Shade.valueOf(shade);
        this.dieColor = DieColor.valueOf(color);
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
     * Returns the dieColor of the lightDie
     * @return the dieColor
     */
    public DieColor getDieColor(){ return this.dieColor; }


    @Override
    public boolean isDie() {
        return true;
    }

    @Override
    public boolean hasColor() {
        return true;
    }
}
