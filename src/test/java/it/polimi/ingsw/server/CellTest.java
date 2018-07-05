package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Die;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class performs tests on the cells of the schema cards
 */
class CellTest {
    private static Cell emptyConstraintCell;
    private static  Cell REDConstraintCell;
    private static Cell GREENConstraintCell;
    private static Cell ONEConstraintCell;
    private static Cell SIXConstraintCell;
    private static Die testDie;

    @BeforeAll
    static void BeforeAllTest(){
        emptyConstraintCell = new Cell();
        REDConstraintCell = new Cell("RED");
        GREENConstraintCell = new Cell("GREEN");
        ONEConstraintCell = new Cell("ONE");
        SIXConstraintCell = new Cell("SIX");
        testDie= new Die("ONE","RED");;
    }

    /**
     * Checks the correct placement of a die in a cell
     */
    @Test
    void GetSetDie() throws IllegalDieException {
        assertNull(emptyConstraintCell.getDie());
        emptyConstraintCell.setDie(testDie);
        assertEquals(testDie,emptyConstraintCell.getDie());
        Die nulldie=null;

        assertThrows(IllegalArgumentException.class,() -> emptyConstraintCell.setDie(null));
    }

    /**
     * Checks if a die is allowed to be placed in a cell with a constraint
     */
    @Test
    void CanAcceptDie(){
        assertTrue(emptyConstraintCell.canAcceptDie(testDie));
        assertTrue(REDConstraintCell.canAcceptDie(testDie));
        assertTrue(ONEConstraintCell.canAcceptDie(testDie));
        assertFalse(GREENConstraintCell.canAcceptDie(testDie));
        assertFalse(SIXConstraintCell.canAcceptDie(testDie));
    }

    /**
     * Tests the constraints getter of the cell
     */
    @Test
    void getConstraint(){
        assertNull(emptyConstraintCell.getConstraint());
        assertEquals("RED",REDConstraintCell.getConstraint().getColor().toString());
        assertEquals("GREEN",GREENConstraintCell.getConstraint().getColor().toString());
        assertEquals("ONE",ONEConstraintCell.getConstraint().getShade().toString());
        assertEquals("SIX",SIXConstraintCell.getConstraint().getShade().toString());
    }

    /**
     * Tests two incorrect placements of non valid die in a cell with constraint enabled
     */
    @Test
    void ExceptionsTest() throws IllegalDieException {
        assertThrows(AssertionError.class,() -> GREENConstraintCell.setDie(testDie));
        assertThrows(AssertionError.class,() -> SIXConstraintCell.setDie(testDie));
    }
}