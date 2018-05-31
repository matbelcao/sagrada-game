package it.polimi.ingsw.server;

import it.polimi.ingsw.server.connection.User;
import it.polimi.ingsw.server.model.Game;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class GameTest {

    @Test
    void testGameConstructor(){
        ArrayList <User> users= new ArrayList<>();
        users.add(new User("Mario", "1234".toCharArray()));
        users.add(new User("Aldo", "4321".toCharArray()));
        users.add(new User("Giovanni", "5678".toCharArray()));
        users.add(new User("Giacomo", "8765".toCharArray()));

        Game game= new Game(users);
        users.clear();
    }
}
