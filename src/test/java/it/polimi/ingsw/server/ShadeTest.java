package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.Shade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShadeTest {
    @Test
    void testContainsMethod(){
        assertTrue(Shade.contains("ONE"));
        assertTrue(Shade.contains("TWO"));
        assertTrue(Shade.contains("THREE"));
        assertTrue(Shade.contains("FOUR"));
        assertTrue(Shade.contains("FIVE"));
        assertTrue(Shade.contains("SIX"));
        assertFalse(Shade.contains("NONE"));
        assertFalse(Shade.contains(""));
        assertFalse(Shade.contains("a")); //randomtest
        assertFalse(Shade.contains(null));
    }
    @Test
    void testvalueOf(){
        assertEquals(Shade.valueOf(1), Shade.ONE);
        assertEquals(Shade.valueOf(2), Shade.TWO);
        assertEquals(Shade.valueOf(3), Shade.THREE);
        assertEquals(Shade.valueOf(4), Shade.FOUR);
        assertEquals(Shade.valueOf(5), Shade.FIVE);
        assertEquals(Shade.valueOf(6), Shade.SIX);
    }
    @Test
    public void testIllegalException() {
        assertThrows(IllegalArgumentException.class,()-> Shade.valueOf(7));
    }
    @Test
    public void testNullException() {
        assertThrows(NullPointerException.class,()-> Shade.valueOf(null));
    }

}
