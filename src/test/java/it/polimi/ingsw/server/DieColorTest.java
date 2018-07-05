package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.DieColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test class performs general tests of the DieColor enum
 */
class DieColorTest {
    @Test
    void testContainsMethod(){
        assertTrue(DieColor.contains("RED"));
        assertTrue(DieColor.contains("GREEN"));
        assertTrue(DieColor.contains("YELLOW"));
        assertTrue(DieColor.contains("BLUE"));
        assertTrue(DieColor.contains("PURPLE"));
        assertTrue(DieColor.contains("NONE"));
        assertFalse(DieColor.contains(""));
        assertFalse(DieColor.contains("a")); //random test case
    }
}
