package it.polimi.ingsw;

/**
 * This class represents a constraint set for a single cell of the Schema Card of the game
 */
public class Constraint {
    private Color color;
    private Face shade;
    private Boolean isColorConstraint;

    /**
     * Creates the object setting the correct type of contraint
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
    }

    /**
     * Get the string name of the shade constraint
     * @return a String that is the name of the shade of the constraint
     */
    public String getShade(){
        return this.shade!=null? this.shade.toString() : null;
    }

    /**
     * Get the string name of the color constraint
     * @return a String that is the name of the color of the die
     */
    public String getColor(){
        return this.color!=null? this.color.toString() : null;
    }

    /**
     * Checks which type of constraint is set
     * @return true if the constraint is a color constraint
     */
    public Boolean isColorConstraint() {
        return this.isColorConstraint;
    }

    /**
     * Creates a printable representation of the constraint for the CLI
     * @return the string containing said representation
     */
    @Override
    public String toString(){
        if(isColorConstraint()){
            return this.color.ansi()+Face.EMPTY+Color.RESET;
        }else{
            return Color.RESET+shade.getUtf();
        }

    }
}
