package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.*;

import java.io.File;
import java.util.ArrayList;

import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private static User u1,u2;
    private static Board board;
    private static PrivObjectiveCard privObjectiveCard;
    private static SchemaCard schema;


    @BeforeAll
    static void setUp() {
        u1 = new User("Mario", "pass1");
        u2 = new User("Luigi", "pass2");

        ArrayList<User> users = new ArrayList<>();
        users.add(u1);
        users.add(u2);

        PubObjectiveCard[] objectiveCards = new PubObjectiveCard[3];
        objectiveCards[0] = new PubObjectiveCard(1, "src" + File.separator + "xml" + File.separator + "PubObjectiveCard.xml");
        objectiveCards[1] = new PubObjectiveCard(2, "src" + File.separator + "xml" + File.separator + "PubObjectiveCard.xml");
        objectiveCards[2] = new PubObjectiveCard(3, "src" + File.separator + "xml" + File.separator + "PubObjectiveCard.xml");

        ToolCard[] toolCards = new ToolCard[3];
        toolCards[0] = new ToolCard(1, "src" + File.separator + "xml" + File.separator + "ToolCard.xml");
        toolCards[1] = new ToolCard(5, "src" + File.separator + "xml" + File.separator + "ToolCard.xml");
        toolCards[2] = new ToolCard(12, "src" + File.separator + "xml" + File.separator + "ToolCard.xml");

        privObjectiveCard = new PrivObjectiveCard(5, "src" + File.separator + "xml" + File.separator + "PrivObjectiveCard.xml");

        board = new Board(users);

        schema = new SchemaCard(1, "src" + File.separator + "xml" + File.separator + "SchemaCard.xml");
    }

    @Test
    void testGameConstructor(){
        Player player = new Player("Mario",0,board,privObjectiveCard);

        assertEquals(0,player.getGameId());
        assertTrue(player.matchesUser(u1));
        assertFalse(player.matchesUser(u2));

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
