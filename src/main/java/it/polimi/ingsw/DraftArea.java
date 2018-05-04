package it.polimi.ingsw;

import java.util.ArrayList;

public class DraftArea {
    ArrayList<Die> drafted;

    DraftArea(){
        drafted = null;
    }

    void addDie(Die die){drafted.add(die);}

    void addDice(ArrayList<Die> dice){
        assert(drafted==null);
        drafted.addAll(dice);
    }

    ArrayList<Die> getDice(){ return drafted; }
}
