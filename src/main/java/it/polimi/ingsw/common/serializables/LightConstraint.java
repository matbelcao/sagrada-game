package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.Shade;

import java.io.Serializable;

/**
 * This class is a lighter, serializable and immutable representation of a Constraint contained in a Cell
 */
public class LightConstraint implements CellContent,Serializable {
    private DieColor dieColor;
    private Shade shade;
    private boolean isColorConstraint;

    public LightConstraint(String constraint){
        if(Shade.contains(constraint)){
            shade = Shade.valueOf(constraint);
            isColorConstraint= false;
        }else{
            dieColor = DieColor.valueOf(constraint);
            isColorConstraint= true;
        }
    }
    /**
     * Creates the object setting the correct type of constraint
     * @param shade a shade constraint
     */
    public LightConstraint(Shade shade) {
        this.shade = shade;
        isColorConstraint = false;
    }

    /**
     * Creates the object setting the correct type of constraint
     * @param dieColor a DieColor constraint
     */
    public LightConstraint(DieColor dieColor) {
        this.dieColor = dieColor;
        isColorConstraint = true;
    }

    /**
     * Returns a string containing the constraint name regardless of the type of constraint
     * @return the name of the value of the constraint
     */
    @Override
    public String toString(){ return  this.isColorConstraint ? this.getDieColor().toString() : this.getShade().toString(); }

    @Override
    public boolean isDie() {
        return false;
    }

    @Override
    public boolean hasColor() {
        return isColorConstraint;
    }

    @Override
    public Shade getShade() {
        return this.shade;
    }

    @Override
    public DieColor getDieColor() {
        return this.dieColor;
    }
}
