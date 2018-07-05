package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.server.model.exceptions.EmptyDiceBagException;
import it.polimi.ingsw.server.model.DiceBag;
import it.polimi.ingsw.server.model.Die;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class checks the dicebag methods
 */
class DiceBagTest {

    /**
     * Tests the constructor and the possibility of extracting a maximum of 90 dice
     */
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
            assertTrue(Shade.contains(testDie.getShade().toString())); //all the shades of the created dice are valid
            switch (testDie.getColor().toString()){
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
        assertThrows(EmptyDiceBagException.class, testBag::draftDie);
    }

    /**
     * Tries to put a dice back in the bag and to take out a new one
     */
    @Test
    void testDrafting(){
        DiceBag testBag = new DiceBag();
        assertEquals(7,testBag.draftDice(7).size());
        Die oldDie=new Die("THREE","NONE");
        assertNotEquals(oldDie.getColor().toString(),testBag.substituteDie(oldDie).getColor().toString());
    }
}
