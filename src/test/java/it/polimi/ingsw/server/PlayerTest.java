package it.polimi.ingsw.server;

import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.connection.User;
import it.polimi.ingsw.server.model.*;

import java.util.ArrayList;

import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private static User u1,u2;
    private static Board board;
    private static SchemaCard schema;


    @BeforeAll
    static void setUp() {
        u1 = new User("Mario", "pass1");
        u2 = new User("Luigi", "pass2");

        ArrayList<User> users = new ArrayList<>();
        users.add(u1);
        users.add(u2);

        board = new Board(users);

        schema = new SchemaCard(1, MasterServer.XML_SOURCE + "SchemaCard.xml");
    }

    @Test
    void testGameConstructor(){
        Player player = board.getPlayer(u1);

        assertEquals(0,player.getGameId());
        assertTrue(player.matchesUser(u1));
        assertFalse(player.matchesUser(u2));

        //Test schema card setter and getter.....
        assertNull(player.getSchema());
        assertEquals(0,player.getFavorTokens());
        player.setSchema(schema);
        assertThrows(AssertionError.class,() -> player.setSchema(schema));
        assertEquals(4,player.getFavorTokens());
        try {
            player.decreaseFavorTokens(2);
        } catch (NegativeTokensException e) {
            e.printStackTrace();
        }
        assertEquals(2,player.getFavorTokens());
    }

}