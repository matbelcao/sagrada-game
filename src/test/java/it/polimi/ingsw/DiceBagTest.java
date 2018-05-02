package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiceBagTest {

    @Test
    void testRep() throws EmptyDiceBagException {
        DiceBag testBag = new DiceBag();
        Die testDie;
        int red = 18;
        int green = 18;
        int yellow = 18;
        int blue = 18;
        int purple = 18;
        for(int i = 0; i<90; i++){
            testDie = testBag.draftDie();
            assertTrue(Face.contains(testDie.getShade())); //all the shades of the created dice are valid
            switch (testDie.getColor()){
                case "RED":
                    red--;
                    break;
                case "GREEN":
                    green--;
                    break;
                case "YELLOW":
                    yellow--;
                    break;
                case "BLUE":
                    blue--;
                    break;
                case "PURPLE":
                    purple--;
                    break;
            }

        }
        assertEquals(0,red);
        assertEquals(0,green);
        assertEquals(0,yellow);
        assertEquals(0,blue);
        assertEquals(0,purple);
        assertThrows(EmptyDiceBagException.class,()->testBag.draftDie());
    }
}
