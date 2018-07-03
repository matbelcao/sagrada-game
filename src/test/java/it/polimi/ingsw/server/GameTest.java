package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.common.serializables.LightSchemaCard;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.controller.Game;
import it.polimi.ingsw.common.exceptions.IllegalActionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    static void testGetter(){
        ArrayList <User> users= new ArrayList<>();
        users.add(new User("Mario", "1234".toCharArray()));
        users.add(new User("Aldo", "4321".toCharArray()));
        users.add(new User("Giovanni", "5678".toCharArray()));
        users.add(new User("Giacomo", "8765".toCharArray()));

        Game game1 = new Game(users,false);
        for (User u:users){
            u.setGame(game1);
        }
        assertEquals(4,game1.getActiveUsers());

        users = (ArrayList<User>) game1.getUsers();
        assertEquals(4,game1.getActiveUsers());

        users.get(3).setStatus(UserStatus.DISCONNECTED);
        assertEquals(3,game1.getActiveUsers());

        User user0 = users.get(0);
        User user1 = users.get(1);
        try {
            assertEquals(4,game1.getDraftedSchemaCards(user0).size());
            game1.choose(user0,2);
            LightSchemaCard schema = game1.getUserSchemaCard(user0);
            assertEquals(schema,game1.getUserSchemaCard(0));
            assertEquals(3, game1.getPubCards().size());
            assertEquals(3, game1.getToolCards().size());
            assertNotEquals(game1.getPrivCard(user1), game1.getPrivCard(user0));
        } catch (IllegalActionException e) {
            e.printStackTrace();
        }

        //The schemacard is still selected
        Executable codeToTest=() ->{game1.getDraftedSchemaCards(user0);};
        assertThrows(IllegalActionException.class,codeToTest);

    }
}
