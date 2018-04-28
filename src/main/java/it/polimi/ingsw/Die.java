package it.polimi.ingsw;

public class Die {
    private Color color;
    private Face shade;

    Die(String value, String color ){

        this.shade = Face.valueOf(value); //make sure value is all CAPS
        this.color = Color.valueOf(color); //make sure color is all CAPS
    }

    public String getColor(){
        return color.toString();
    }

    public String getShade(){
        return shade.toString();
    }

    @Override
    public String toString(){
        return color.ansi()+shade.ascii()+Color.RESET;
    }
}
