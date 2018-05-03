package it.polimi.ingsw;

/**
 * This class represents a constraint set for a single cell of the Schema Card of the game
 */
public class Constraint {
    private Color color;
    private Face shade;
    private Boolean isColorConstraint;
    private Boolean isActive;

    /**
     * Creates the object setting the correct type of constraint
     * @param constraint a String describing the constraint (whether it is a color or a shade)
     */
    Constraint(String constraint){
        if(Face.contains(constraint)){
            shade = Face.valueOf(constraint);
            isColorConstraint= Boolean.FALSE;
        }else{
            color = Color.valueOf(constraint);
            isColorConstraint= Boolean.TRUE;
        }
        isActive=true;
    }

    /**
     * Get the Face of the constraint if it is a shade constraint
     * @return the shade of the constraint
     */
    public Face getShade(){
        return !this.isColorConstraint()? this.shade : null;
    }

    /**
     * Get the Color of the constraint if it is a color constraint
     * @return  the Color of the constraint
     */
    public Color getColor(){ return this.isColorConstraint()? this.color : null; }

    /**
     * Checks which type of constraint is set
     * @return true if the constraint is a color constraint
     */
    public Boolean isColorConstraint() {
        return this.isColorConstraint;
    }

    /**
     * Checks if constraint is active
     * @return true if the constraint is a color constraint
     */
    public Boolean isActive() {
        return this.isActive;
    }

    /**
     * Returns a string containing the constraint name regardless of the type of constraint
     * @return the name of the value of the constraint
     */
    @Override
    public String toString(){ return  this.isColorConstraint() ? this.getColor().toString() : this.getShade().toString(); }

    /**
     * Changes the status of the constraint
     * @param status the new status of activity of the constraint
     */
    public void setActive(Boolean status){
        this.isActive= status;
    }

    /**
     * Creates a printable representation of the constraint for the CLI
     * @return the string containing said representation
     */
    public String toUtf(){
        if(isColorConstraint()){
            return this.color.ansi()+Face.EMPTY+Color.RESET;
        }else{
            return Color.RESET+shade.getUtf();
        }

    }
}
