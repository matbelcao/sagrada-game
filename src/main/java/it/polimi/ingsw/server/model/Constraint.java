package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.immutables.LightConstraint;
import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Face;

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
    public Constraint(Face shade) {
        this.constraint= new LightConstraint(shade);

    }

    /**
     * Creates the object setting the correct type of constraint
     * @param color a Color constraint
     */
    public Constraint(Color color) {
        this.constraint= new LightConstraint(color);
    }

    /**
     * Get the Face of the constraint if it is a shade constraint
     * @return the shade of the constraint
     */
    public Face getShade(){
        return this.constraint.getShade();
    }

    /**
     * Get the Color of the constraint if it is a color constraint
     * @return  the Color of the constraint
     */
    public Color getColor(){ return this.constraint.getColor(); }

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
            return this.constraint.getColor().getUtf()+Face.EMPTY+Color.NONE.getUtf();
        }else{
            return Color.NONE.getUtf() +this.constraint.getShade().getUtf();
        }

    }
}
