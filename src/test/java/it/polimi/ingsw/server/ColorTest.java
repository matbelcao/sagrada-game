package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.Color;
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
        assertTrue(Color.contains("NONE"));
        assertFalse(Color.contains(""));
        assertFalse(Color.contains("a")); //random test case

    }

}
