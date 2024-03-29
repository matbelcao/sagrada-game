package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.controller.Game;
import it.polimi.ingsw.server.model.iterators.RoundIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class checks the iterator of the match's players
 */
class RoundIteratorTest {
    private static Game controller;
    private static ArrayList<User> users;
    private static RoundIterator round;

    @BeforeAll
    static void setUp(){
        users=new ArrayList<>();
        users.add(new User("giuda","santana".toCharArray()));
        users.add(new User("marcello","password1".toCharArray()));
        users.add(new User("luca","qwerty".toCharArray()));
        controller = new Game(users,false);
        controller.createBoard();
    }
    @BeforeEach
    void initRoundIterator(){
        round = (RoundIterator) controller.iterator();
    }

    /**
     * Tests that the round iterator do exactly 10 rounds
     */
    @Test
    void testRoundIteratorWhile() {
        int turns=0;
        int rounds=0;
        while(round.hasNextRound()){
            round.nextRound();
            rounds++;
            while(round.hasNext()){
                round.next();
                turns++;
            }
        }
        assertEquals(60, turns);
        assertEquals(10, rounds);
        assertEquals(9, round.getRoundNumber());
    }

    /**
     * Checks the execution flow of the rounds and shifts up to the tenth
     */
    @Test
    void testRoundIteratorRules() {
        User next;
        //round 1
        next=round.next();
        assertTrue(round.isFirstTurn());
        assertEquals(users.get(0),next);
        next=round.next();
        assertTrue(round.isFirstTurn());
        assertEquals(0,round.getRoundNumber());
        assertEquals(users.get(1),next);
        next=round.next();
        assertTrue(round.isFirstTurn());
        assertEquals(users.get(2),next);
        next=round.next();
        assertFalse(round.isFirstTurn());
        assertEquals(users.get(2),next);
        next=round.next();
        assertFalse(round.isFirstTurn());
        assertEquals(users.get(1),next);
        assertFalse(round.isFirstTurn());
        next=round.next();
        assertEquals(users.get(0),next);

        Executable codeToTest = () -> {
            round.next();
        };
        assertThrows(NoSuchElementException.class, codeToTest);
        assertTrue(!round.hasNext());
        //round 2
        round.nextRound();
        assertEquals(1,round.getRoundNumber());
        next=round.next();
        assertEquals(users.get(1),next);
        next=round.next();
        assertEquals(users.get(2),next);
        next=round.next();
        assertEquals(users.get(0),next);
        next=round.next();
        assertEquals(users.get(0),next);
        assertTrue(round.hasNext());
        next=round.next();
        assertEquals(users.get(2),next);

        //round 3
        round.nextRound();
        next=round.next();
        assertEquals(users.get(2),next);
        next=round.next();
        assertEquals(users.get(0),next);
        next=round.next();
        assertEquals(users.get(1),next);
        next=round.next();
        assertEquals(users.get(1),next);
        assertTrue(round.hasNext());
        //round 4
        round.nextRound();
        next=round.next();
        assertEquals(users.get(0),next);
        next=round.next();
        assertEquals(users.get(1),next);
        next=round.next();
        assertEquals(users.get(2),next);
        next=round.next();
        assertEquals(users.get(2),next);
        assertTrue(round.hasNext());
        //round 5
        round.nextRound();
        next=round.next();
        assertEquals(users.get(1),next);
        next=round.next();
        assertEquals(users.get(2),next);
        next=round.next();
        assertEquals(users.get(0),next);
        next=round.next();
        assertEquals(users.get(0),next);
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
        assertEquals(users.get(1),next);
        next=round.next();
        assertEquals(users.get(2),next);
        next=round.next();
        assertEquals(users.get(0),next);
        next=round.next();
        assertEquals(users.get(0),next);
        assertTrue(round.hasNext());

        round.next();
        round.next();
        assertTrue(!round.hasNext());
        assertTrue(round.hasNextRound());

        //round 9
        round.nextRound();
        //round 10
        round.nextRound();
        //no more rounds
        assertEquals(9,round.getRoundNumber());
        assertTrue(!round.hasNextRound());

        codeToTest = () -> {
            round.nextRound();
        };
        Assertions.assertThrows(NoSuchElementException.class, codeToTest);

        assertEquals(9,round.getRoundNumber());
    }

    @Test
     void testGetRoundNumber() throws NoSuchMethodException {

        //round 1
        assertEquals(0,round.getRoundNumber());
        //round 2
        round.nextRound();
        assertEquals(1,round.getRoundNumber());
        //round 3
        round.nextRound();
        assertEquals(2,round.getRoundNumber());
        //round 4
        round.nextRound();
        assertEquals(3,round.getRoundNumber());
        //round 5
        round.nextRound();
        assertEquals(4,round.getRoundNumber());
        //round 6
        round.nextRound();
        assertEquals(5,round.getRoundNumber());
        assertTrue(round.hasNextRound());
        //round 7
        round.nextRound();
        assertEquals(6,round.getRoundNumber());
        //round 8
        round.nextRound();
        assertEquals(7,round.getRoundNumber());
        //round 9
        round.nextRound();
        assertEquals(8,round.getRoundNumber());
        //round 10
        round.nextRound();
        assertEquals(9,round.getRoundNumber());
        assertTrue(!round.hasNextRound());

        Executable codeToTest = round::nextRound;
        Assertions.assertThrows(NoSuchElementException.class, codeToTest);
        assertEquals(9,round.getRoundNumber());
    }

}
