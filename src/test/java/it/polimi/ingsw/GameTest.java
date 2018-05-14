package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class GameTest {

    @Test
    void testGameConstructor(){
        ArrayList <User> users= new ArrayList<>();
        users.add(new User("Mario", "1234"));
        users.add(new User("Aldo", "4321"));
        users.add(new User("Giovanni", "5678"));
        users.add(new User("Giacomo", "8765"));

        Game game= new Game(users);
        users.clear();
    }
}
