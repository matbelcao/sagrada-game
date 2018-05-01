package it.polimi.ingsw;

public class PrivObjectiveCard extends ObjectiveCard{
    String color;

    public PrivObjectiveCard(int id, String xmlSrc){
        super(id,xmlSrc,"PrivObjectiveCard");
        switch (super.getId()){
            case 1:
                color=new String("RED");
                break;
            case 2:
                color=new String("YELLOW");
                break;
            case 3:
                color=new String("GREEN");
                break;
            case 4:
                color=new String("BLUE");
                break;
            case 5:
                color=new String("PURPLE");
                break;
        }
    }

    public String getColor(){
        return new String(color);
    }
}
