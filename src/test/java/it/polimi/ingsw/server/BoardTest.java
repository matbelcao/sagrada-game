package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.User;
import it.polimi.ingsw.server.model.Board;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private static ArrayList<User> users1,users2;
    private static User u3,u6;
    private static boolean additionalSchemas=false;

    @BeforeAll
    static void setUp(){
        User u1 = new User("Mario", "pass1".toCharArray());
        User u2 = new User("Luigi", "pass2".toCharArray());
        u3 = new User("Giovanni", "pass3".toCharArray());
        User u4 = new User("Giacomo", "pass4".toCharArray());
        User u5 = new User("Aldo", "pass5".toCharArray());
        u6 = new User("Paolo", "pass6".toCharArray());

        users1=new ArrayList<>();
        users2=new ArrayList<>();

        users1.add(u1);
        users1.add(u2);
        users1.add(u3);
        users1.add(u4);

        users2.add(u5);
        users2.add(u6);
    }

    @Test
    void testBoardConstructor(){
        Board board1=new Board(users1, additionalSchemas);
        Board board2=new Board(users2, additionalSchemas);


        assertEquals(2, board1.getPlayer(u3).getGameId());

        assertEquals(1, board2.getPlayer(u6).getGameId());
        assertFalse(board2.getPlayer(u6).matchesUser(u3));
        assertTrue(board2.getPlayer(u6).matchesUser(u6));
    }

    @Test
    void testDraftSchemas(){
        Board board1=new Board(users1, additionalSchemas);
        Board board2=new Board(users2, additionalSchemas);

        assertEquals(16,board1.draftSchemas().length);
        assertEquals(8,board2.draftSchemas().length);
    }
}
