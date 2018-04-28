package it.polimi.ingsw;

public class Constraint {
    private Color color;
    private Face shade;
    private Boolean isColorConstraint;

    Constraint(String constraint){
        if(Face.contains(constraint)){
            shade = Face.valueOf(constraint);
            isColorConstraint= Boolean.FALSE;
        }else{
            color = Color.valueOf(constraint);
            isColorConstraint= Boolean.TRUE;
        }
    }

    public String getShade(){
        return shade.toString();
    }

    public String getColor(){
        return color.toString();
    }

    public Boolean isColorConstraint() {
        return isColorConstraint;
    }

    @Override
    public String toString(){
        if(isColorConstraint()){
            return color.ansi()+Face.EMPTY+Color.RESET;
        }else{
            return Color.RESET+shade.ascii();
        }

    }
}
