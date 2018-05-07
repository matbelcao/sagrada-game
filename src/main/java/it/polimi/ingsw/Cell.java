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

        if( this.constraint.isColorConstraint() && die.getColor().toString().equals(this.constraint.getColor().toString())){
            return true;
        }

        return !this.constraint.isColorConstraint() && die.getShade().toString().equals(this.constraint.getShade().toString());

    }

    /**
     * Tests whether a die respects the Cell specific constraint
     * @param die die to be checked if it can be possibly be placed in the Cell.
     * @param ignoreConstraint string that signals if one type or all types of constraint can be ignored
     * @return true iff the die respects the Cell constraint
     */
    public Boolean canAcceptDie(Die die,String ignoreConstraint){

        if(ignoreConstraint.equals("ALL")){ return true; }
        if(ignoreConstraint.equals("COLOR") && this.constraint.isColorConstraint()){ return true; }
        if(ignoreConstraint.equals("SHADE") && !this.constraint.isColorConstraint()){ return true; }

        if(this.constraint==null || !this.constraint.isActive()) {
            return true;
        }

        if( this.constraint.isColorConstraint() && die.getColor().toString().equals(this.constraint.getColor().toString())){
            return true;
        }

        return !this.constraint.isColorConstraint() && die.getShade().toString().equals(this.constraint.getShade().toString());

    }

    /**
     * Sets the new die in place.
     * @param die die to be placed in the Cell
     */
    public void setDie(Die die) {
        assert canAcceptDie(die);
            this.die=die;
    }

    /**
     * Sets the new die in place.
     * @param die die to be placed in the Cell
     */
    public void setDie(Die die,String ignoreConstraint) {
        assert canAcceptDie(die,ignoreConstraint);
        this.die=die;
    }

    public Boolean checkNeighbor(Die die){
        return (this.getDie().getColor().toString().equals(die.getColor().toString()) || this.getDie().getShade().toString().equals(die.getShade().toString()));
    }

    /**
     * Allows to get the die placed in the Cell
     * @return the Cell's die
     */
    public Die getDie(){
        return this.die;
    }

    /**
     * Returns whether or not the cell has a constraint
     * @return true iff the cell has a constraint
     */
    public Boolean hasConstraint(){ return this.constraint!=null; }

    /**
     * Returns whether or not the cell has a constraint
     * @return true iff the cell has a constraint
     */
    public Boolean hasDie(){ return this.die!=null; }

    /**
     * Returns the cell's constraint
     * @return cell's constraint
     */
    public Constraint getConstraint(){
        return this.constraint;
    }

}