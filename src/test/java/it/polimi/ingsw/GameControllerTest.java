package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameControllerTest {
    @BeforeAll
    void setUp(){
        ArrayList<Player> players=new ArrayList<>();
        players.add(new Player("giuda","santana"));
        players.add(new Player("marcello","password1"));
        players.add(new Player("luca","qwerty"));


    }
}
