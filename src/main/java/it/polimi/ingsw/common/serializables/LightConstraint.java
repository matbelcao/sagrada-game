package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.server.model.Constraint;

import java.io.Serializable;

public class LightConstraint implements CellContent,Serializable {
    private Color color;
    private Shade shade;
    private boolean isColorConstraint;

    public LightConstraint(String constraint){
        if(Shade.contains(constraint)){
            shade = Shade.valueOf(constraint);
            isColorConstraint= false;
        }else{
            color = Color.valueOf(constraint);
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
     * @param color a Color constraint
     */
    public LightConstraint(Color color) {
        this.color = color;
        isColorConstraint = true;
    }

    /**
     * builds a LightConstraint from a constraint
     * @param constr the constraint to be copied
     * @return the LightConstraint
     */
    public static LightConstraint toLightConstraint(Constraint constr){
        return new LightConstraint((constr.isColorConstraint() ? constr.getColor().toString() : constr.getShade().toString()));
    }
    /**
     * Returns a string containing the constraint name regardless of the type of constraint
     * @return the name of the value of the constraint
     */
    @Override
    public String toString(){ return  this.isColorConstraint ? this.getColor().toString() : this.getShade().toString(); }

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
    public Color getColor() {
        return this.color;
    }
}
