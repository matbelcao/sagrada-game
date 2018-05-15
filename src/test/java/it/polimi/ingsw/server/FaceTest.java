package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.exceptions.IllegalShadeException;
import it.polimi.ingsw.server.model.enums.Face;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FaceTest {
    @Test
    void testContainsMethod(){
        assertTrue(Face.contains("ONE"));
        assertTrue(Face.contains("TWO"));
        assertTrue(Face.contains("THREE"));
        assertTrue(Face.contains("FOUR"));
        assertTrue(Face.contains("FIVE"));
        assertTrue(Face.contains("SIX"));
        assertFalse(Face.contains("RESET"));
        assertFalse(Face.contains(""));
        assertFalse(Face.contains("a")); //randomtest
        assertFalse(Face.contains(null));
    }
    @Test
    void testvalueOf() throws IllegalShadeException {
        assertEquals(Face.valueOf(1), Face.ONE);
        assertEquals(Face.valueOf(2), Face.TWO);
        assertEquals(Face.valueOf(3), Face.THREE);
        assertEquals(Face.valueOf(4), Face.FOUR);
        assertEquals(Face.valueOf(5), Face.FIVE);
        assertEquals(Face.valueOf(6), Face.SIX);
    }
    @Test
    public void testIllegalException() {
        assertThrows(IllegalShadeException.class,()-> Face.valueOf(7));
    }
    @Test
    public void testNullException() {
        assertThrows(NullPointerException.class,()-> Face.valueOf(null));
    }

}
