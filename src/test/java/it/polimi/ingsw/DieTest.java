package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DieTest {
    private static Die test;

    @BeforeEach
    void beforeEachTests() {
        test = new Die("ONE", "RED");
    }

    @Test
    void init() {
        assertEquals("ONE", test.getShade());
        assertEquals("RED", test.getColor());
        assertEquals("\u001B[31m\u2680\u001B[0m", test.toUtf());
    }

    @Test
    void testIncreaseDecrease() throws IllegalShadeException {
        test.increaseShade();
        assertEquals("TWO", test.getShade());
        assertEquals("RED", test.getColor());
        test.decreaseShade();
        assertEquals("ONE", test.getShade());
        assertEquals("RED", test.getColor());
    }

    @Test
    void testSetShade() throws IllegalShadeException {
        test.setShade(6);
        assertEquals("SIX", test.getShade());
        assertEquals("RED", test.getColor());
    }

    @Test
    void testflip() throws IllegalShadeException {
        test.flipShade();
        assertEquals("SIX", test.getShade());
        assertEquals("RED", test.getColor());
        test.flipShade();
        assertEquals("ONE", test.getShade());
        test.setShade(2);
        test.flipShade();
        assertEquals("FIVE", test.getShade());
        test.flipShade();
        assertEquals("TWO", test.getShade());
        test.setShade(3);
        test.flipShade();
        assertEquals("FOUR", test.getShade());
        test.flipShade();
        assertEquals("THREE", test.getShade());
    }

    @Test
    void TestToString(){
        assertEquals("RED" + File.separator + "ONE",test.toString());
    }

    @Test
    void decreaseException() {
        assertThrows(IllegalShadeException.class,() -> test.decreaseShade());
    }

    @Test
    void increaseException() throws IllegalShadeException {
        test.setShade(6);
        assertThrows(IllegalShadeException.class,() -> test.increaseShade());
    }

    @Test
    void setException(){
        assertThrows(IllegalShadeException.class,() ->test.setShade(0));
    }

}
