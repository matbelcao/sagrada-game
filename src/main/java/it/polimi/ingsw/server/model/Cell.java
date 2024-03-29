package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.model.enums.IgnoredConstraint;

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

        return canAcceptDie(die,IgnoredConstraint.NONE);
    }

    /**
     * Tests whether a die respects the Cell specific constraint
     * @param die die to be checked if it can be possibly be placed in the Cell.
     * @param ignoreConstraint signals if none, one type or all types of constraint can be ignored
     * @return true iff the die respects the Cell constraint
     */
    Boolean canAcceptDie(Die die, IgnoredConstraint ignoreConstraint){
        if(die==null){throw new IllegalArgumentException();}
        if(this.constraint==null ) {
            return true;
        }

        if(ignoreConstraint.equals(IgnoredConstraint.NONE)){
            if( this.constraint.isColorConstraint() && die.getColor().toString().equals(this.constraint.getColor().toString())){
                return true;
            }
            return !this.constraint.isColorConstraint() && die.getShade().toString().equals(this.constraint.getShade().toString());
        }
        if(ignoreConstraint.equals(IgnoredConstraint.ALL)){ return true; }
        if(ignoreConstraint.equals(IgnoredConstraint.COLOR) && this.constraint.isColorConstraint()){ return true; }
        if(ignoreConstraint.equals(IgnoredConstraint.SHADE) && !this.constraint.isColorConstraint()){ return true; }


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
        setDie(die,IgnoredConstraint.NONE);
    }

    /**
     * Removes the die that was placed in the cell if it has one
     * @return a pointer to the die that is being removed
     */
    public Die removeDie() {
        Die tempDie;
        assert this.hasDie();
        tempDie=this.die;
        this.die=null;
        return tempDie;
    }

    /**
     * Sets the new die in place.
     * @param die die to be placed in the Cell
     */
    void setDie(Die die, IgnoredConstraint ignoreConstraint) {

        assert canAcceptDie(die,ignoreConstraint) || ignoreConstraint.equals(IgnoredConstraint.FORCE);
        this.die=die;
    }

    Boolean checkNeighbor(Die die){
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