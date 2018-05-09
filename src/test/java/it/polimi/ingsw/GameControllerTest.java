package it.polimi.ingsw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.NoSuchElementException;


public class GameControllerTest {
    private static GameController controller;
    private static ArrayList<Player> players;
    @BeforeAll
    static void setUp(){

        players=new ArrayList<>();
        players.add(new Player("giuda","santana"));
        players.add(new Player("marcello","password1"));
        players.add(new Player("luca","qwerty"));
        controller = new GameController(players);
        controller.createBoard();
    }

    @Test
    void testRoundIterator() throws NoSuchMethodException {
        RoundIterator round = (RoundIterator) controller.iterator();
        //round 1
        Player next = round.next();
        assertEquals(players.get(0),next);
        next=round.next();
        assertEquals(players.get(1),next);
        next=round.next();
        assertEquals(players.get(2),next);
        next=round.next();
        assertEquals(players.get(2),next);
        next=round.next();
        assertEquals(players.get(1),next);
        next=round.next();
        assertEquals(players.get(0),next);

        Executable codeToTest = () -> {
            round.next();
        };
        assertThrows(NoSuchElementException.class, codeToTest);
        assertTrue(!round.hasNext());
        //round 2
        round.nextRound();
        next=round.next();
        assertEquals(players.get(1),next);
        next=round.next();
        assertEquals(players.get(2),next);
        next=round.next();
        assertEquals(players.get(0),next);
        next=round.next();
        assertEquals(players.get(0),next);
        assertTrue(round.hasNext());
        next=round.next();
        assertEquals(players.get(2),next);

        //round 3
        round.nextRound();
        next=round.next();
        assertEquals(players.get(2),next);
        next=round.next();
        assertEquals(players.get(0),next);
        next=round.next();
        assertEquals(players.get(1),next);
        next=round.next();
        assertEquals(players.get(1),next);
        assertTrue(round.hasNext());
        //round 4
        round.nextRound();
        next=round.next();
        assertEquals(players.get(0),next);
        next=round.next();
        assertEquals(players.get(1),next);
        next=round.next();
        assertEquals(players.get(2),next);
        next=round.next();
        assertEquals(players.get(2),next);
        assertTrue(round.hasNext());
        //round 5
        round.nextRound();
        next=round.next();
        assertEquals(players.get(1),next);
        next=round.next();
        assertEquals(players.get(2),next);
        next=round.next();
        assertEquals(players.get(0),next);
        next=round.next();
        assertEquals(players.get(0),next);
        assertTrue(round.hasNext());

        round.next();
        round.next();
        assertTrue(!round.hasNext());

        //round 6
        round.nextRound();
        //round 7
        round.nextRound();
        //round 8
        round.nextRound();
        next=round.next();
        assertEquals(players.get(1),next);
        next=round.next();
        assertEquals(players.get(2),next);
        next=round.next();
        assertEquals(players.get(0),next);
        next=round.next();
        assertEquals(players.get(0),next);
        assertTrue(round.hasNext());

        round.next();
        round.next();
        assertTrue(!round.hasNext());
        assertTrue(round.hasNextRound());
        //round 9
        round.nextRound();
        assertEquals(8,round.getRoundNumber());
        //round 10
        round.nextRound();
        //no more rounds
        assertTrue(!round.hasNextRound());

        codeToTest = () -> {
            round.nextRound();
        };
        Assertions.assertThrows(NoSuchElementException.class, codeToTest);

        assertEquals(9,round.getRoundNumber());

    }
}
