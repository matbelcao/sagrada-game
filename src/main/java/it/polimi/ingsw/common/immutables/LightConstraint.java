package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Face;

public class LightConstraint implements CellContent {
    private Color color;
    private Face shade;
    private boolean isColorConstraint;

    public LightConstraint(String constraint){
        if(Face.contains(constraint)){
            shade = Face.valueOf(constraint);
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
    public LightConstraint(Face shade) {
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
    public Face getShade() {
        return this.shade;
    }

    @Override
    public Color getColor() {
        return this.color;
    }
}
