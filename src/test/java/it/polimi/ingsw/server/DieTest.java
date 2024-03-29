package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.exceptions.IllegalShadeException;
import it.polimi.ingsw.server.model.Die;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class checks the correct instantiation of the dice and the relative methods to manipulate it
 */
class DieTest {
    private static Die test;

    @BeforeEach
    void beforeEachTests() {
        test = new Die("ONE", "RED");
    }

    /**
     * Tests the correct instantiation of a die (part 1)
     */
    @Test
    void testAltConstructor(){
        Die die=new Die(1,"RED");
        assertEquals("ONE", die.getShade().toString());
        assertEquals("RED", die.getColor().toString());
    }

    /**
     * Tests the correct instantiation of a die (part 2)
     */
    @Test
    void init() {
        assertEquals("ONE", test.getShade().toString());
        assertEquals("RED", test.getColor().toString());
        assertEquals("\u001B[91m\u2680\u001B[0m", test.toUtf());
    }

    /**
     * Tests the correct action of increasing/decreasing the shade of the die
     */
    @Test
    void testIncreaseDecrease() throws IllegalShadeException {
        test.increaseShade();
        assertEquals("TWO", test.getShade().toString());
        assertEquals("RED", test.getColor().toString());
        test.decreaseShade();
        assertEquals("ONE", test.getShade().toString());
        assertEquals("RED", test.getColor().toString());
    }

    /**
     * Tests the correct action of setting the shade of the die
     */
    @Test
    void testSetShade() throws IllegalShadeException {
        test.setShade(6);
        assertEquals("SIX", test.getShade().toString());
        assertEquals("RED", test.getColor().toString());
    }

    /**
     * Tests the correct action of flipping the shade of the die
     */
    @Test
    void testflip() throws IllegalShadeException {
        test.flipShade();
        assertEquals("SIX", test.getShade().toString());
        assertEquals("RED", test.getColor().toString());
        test.flipShade();
        assertEquals("ONE", test.getShade().toString());
        test.setShade(2);
        test.flipShade();
        assertEquals("FIVE", test.getShade().toString());
        test.flipShade();
        assertEquals("TWO", test.getShade().toString());
        test.setShade(3);
        test.flipShade();
        assertEquals("FOUR", test.getShade().toString());
        test.flipShade();
        assertEquals("THREE", test.getShade().toString());
    }

    /**
     * Checks the toString() overrided method
     */
    @Test
    void TestToString(){
        assertEquals("RED" + File.separator + "ONE",test.toString());
    }

    /**
     * Tests the correct throwing exception if the die has shade ONE
     */
    @Test
    void decreaseException() {
        assertThrows(IllegalShadeException.class,() -> test.decreaseShade());
    }

    /**
     * Tests the correct throwing exception if the die has shade SIX
     */
    @Test
    void increaseException(){
        test.setShade(6);
        assertThrows(IllegalShadeException.class,() -> test.increaseShade());
    }

    /**
     * Tests the correct throwing exception if the shade to set is not valid
     */
    @Test
    void setException(){
        assertThrows(IllegalArgumentException.class,() ->test.setShade(0));
    }

    /**
     * Tests the correct action of swapping the shade of the die
     */
    @Test
    void testSwap(){
        Die die1=new Die("ONE","RED");
        Die die2=new Die("SIX","GREEN");

        die1.swap(die2);

        assertEquals("SIX",die1.getShade().toString());
        assertEquals("GREEN",die1.getColor().toString());
        assertEquals("ONE",die2.getShade().toString());
        assertEquals("RED",die2.getColor().toString());
    }

}
