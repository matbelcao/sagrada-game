package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColorTest {
    @Test
    void testContainsMethod(){
        assertTrue(Color.contains("RED"));
        assertTrue(Color.contains("GREEN"));
        assertTrue(Color.contains("YELLOW"));
        assertTrue(Color.contains("BLUE"));
        assertTrue(Color.contains("PURPLE"));
        assertFalse(Color.contains("RESET"));
        assertFalse(Color.contains(""));
        assertFalse(Color.contains("a")); //random test case

    }

}
