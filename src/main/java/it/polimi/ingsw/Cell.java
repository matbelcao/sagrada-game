package it.polimi.ingsw;



/**
 * This class represents a Cell in Schema Cards, it can contain a constraint and/or a die.
 *
 */

public class Cell {
    private Constraint constraint;
    private Die die;

    /**
     * Default constructor
     */
    public Cell(){
        this.constraint=null;
        this.die=null;
    }

    /**
     * Initializes a new cell with its constraint
     * @param constraint name of the constraint
     */
    public Cell(String constraint){
        this.constraint = new Constraint(constraint);
        this.die=null;
    }

    /**
     * Tests whether a die respects the Cell specific constraint
     * @param die die to be checked if it can be possibly be placed in the Cell.
     * @return true iff the die respects the Cell constraint
     */
    public Boolean canAcceptDie(Die die){
        if(this.constraint==null || !this.constraint.isActive()) {
            return true;
        }

        if(this.constraint.isColorConstraint()){
            if(die.getColor().equals(this.constraint.getColor())){
                return true;
            }
        }

        if(!this.constraint.isColorConstraint()) {
            if (die.getShade().equals(this.constraint.getShade())){
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the new die in place.
     * @param die die to be placed in the Cell
     * @throws IllegalDieException if the die you're trying to place doesn't respect the constraint (this shouldn't be happening in the first place)
     */
    public void setDie(Die die) throws IllegalDieException {
        if(canAcceptDie(die)){
            this.die=die;
        }else{
            throw new IllegalDieException();
        }
    }

    /**
     * Allows to get the die placed in the Cell
     * @return the Cell's die
     */
    public Die getDie(){
        return this.die;
    }

    /**
     * Returns the cell's constraint
     * @return cell's constraint
     */
    public Constraint getConstraint(){
        return this.constraint;
    }

}