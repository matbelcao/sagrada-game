package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.Die;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    static Cell emptyConstraintCell;
    static  Cell REDConstraintCell;
    static Cell GREENConstraintCell;
    static Cell ONEConstraintCell;
    static Cell SIXConstraintCell;
    static Die testDie;

    @BeforeAll
    static void BeforeAllTest(){
        emptyConstraintCell = new Cell();
        REDConstraintCell = new Cell("RED");
        GREENConstraintCell = new Cell("GREEN");
        ONEConstraintCell = new Cell("ONE");
        SIXConstraintCell = new Cell("SIX");
        testDie= new Die("ONE","RED");;
    }
    @Test
    void GetSetDie() throws IllegalDieException {
        assertEquals(null,emptyConstraintCell.getDie());
        emptyConstraintCell.setDie(testDie);
        assertEquals(testDie,emptyConstraintCell.getDie());
        Die nulldie=null;

        assertThrows(AssertionError.class,() -> emptyConstraintCell.setDie(nulldie));
    }

    @Test
    void CanAcceptDie(){
        assertTrue(emptyConstraintCell.canAcceptDie(testDie));
        assertTrue(REDConstraintCell.canAcceptDie(testDie));
        assertTrue(ONEConstraintCell.canAcceptDie(testDie));
        assertFalse(GREENConstraintCell.canAcceptDie(testDie));
        assertFalse(SIXConstraintCell.canAcceptDie(testDie));
    }

    @Test
    void getConstraint(){
        assertEquals(null,emptyConstraintCell.getConstraint());
        assertEquals("RED",REDConstraintCell.getConstraint().getColor().toString());
        assertEquals("GREEN",GREENConstraintCell.getConstraint().getColor().toString());
        assertEquals("ONE",ONEConstraintCell.getConstraint().getShade().toString());
        assertEquals("SIX",SIXConstraintCell.getConstraint().getShade().toString());
    }

    @Test
    void ExceptionsTest() throws IllegalDieException {
        assertThrows(AssertionError.class,() -> GREENConstraintCell.setDie(testDie));
        assertThrows(AssertionError.class,() -> SIXConstraintCell.setDie(testDie));
    }
}