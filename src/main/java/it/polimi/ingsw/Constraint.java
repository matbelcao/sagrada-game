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
     * Get the string name of the shade constraint
     * @return a String that is the name of the shade of the constraint
     */
    public String getShade(){
        return !this.isColorConstraint()? this.shade.toString() : "null";
    }

    /**
     * Get the string name of the color constraint
     * @return a String that is the name of the color of the die
     */
    public String getColor(){ return this.isColorConstraint()? this.color.toString() : "null"; }

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
    public String toString(){ return  this.isColorConstraint() ? this.getColor() : this.getShade(); }

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
