package it.polimi.ingsw;


public class Cell {
    private Constraint constraint;
    private Die die;


    public Boolean canAcceptDie(Die die){
        if(this.constraint==null) {
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

    public void setDie(Die die) throws IllegalDieException {
        if(canAcceptDie(die)){
            this.die=die;
        }else{
            throw new IllegalDieException();
        }
    }

    public Die getDie(){
        return this.die;
    }

    public void setConstraint( Constraint constraint){
        this.constraint=constraint;
    }
}