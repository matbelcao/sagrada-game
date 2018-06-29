package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.serializables.LightConstraint;
import it.polimi.ingsw.common.enums.Shade;

/**
 * This class represents a constraint set for a single cell of the Schema Card of the game
 */
public class Constraint {
    private LightConstraint constraint;

    /**
     * Creates the object setting the correct type of constraint
     * @param constraint a String describing the constraint (whether it is a color or a shade)
     */
    public Constraint(String constraint){
        this.constraint= new LightConstraint(constraint);

    }

    /**
     * Creates the object setting the correct type of constraint
     * @param shade a shade constraint
     */
    public Constraint(Shade shade) {
        this.constraint= new LightConstraint(shade);

    }

    /**
     * Creates the object setting the correct type of constraint
     * @param dieColor a DieColor constraint
     */
    public Constraint(DieColor dieColor) {
        this.constraint= new LightConstraint(dieColor);
    }

    /**
     * Get the Shade of the constraint if it is a shade constraint
     * @return the shade of the constraint
     */
    public Shade getShade(){
        return this.constraint.getShade();
    }

    /**
     * Get the DieColor of the constraint if it is a color constraint
     * @return  the DieColor of the constraint
     */
    public DieColor getColor(){ return this.constraint.getDieColor(); }

    /**
     * Checks which type of constraint is set
     * @return true if the constraint is a color constraint
     */
    public Boolean isColorConstraint() {
        return this.constraint.hasColor();
    }


    /**
     * Returns a string containing the constraint name regardless of the type of constraint
     * @return the name of the value of the constraint
     */
    @Override
    public String toString(){ return  this.isColorConstraint() ? this.getColor().toString() : this.getShade().toString(); }


    /**
     * Creates a printable representation of the constraint for the CLI
     * @return the string containing said representation
     */
    public String toUtf(){
        if(isColorConstraint()){
            return this.constraint.getDieColor().getUtf()+Shade.EMPTY+ DieColor.NONE.getUtf();
        }else{
            return DieColor.NONE.getUtf() +this.constraint.getShade().getUtf();
        }

    }
}
