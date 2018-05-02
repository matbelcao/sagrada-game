package it.polimi.ingsw;

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
        emptyConstraintCell.setDie(null);
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
        assertEquals("RED",REDConstraintCell.getConstraint().getColor());
        assertEquals("GREEN",GREENConstraintCell.getConstraint().getColor());
        assertEquals("ONE",ONEConstraintCell.getConstraint().getShade());
        assertEquals("SIX",SIXConstraintCell.getConstraint().getShade());
    }

    @Test
    void ExceptionsTest() throws IllegalDieException {
        assertThrows(IllegalDieException.class,() -> GREENConstraintCell.setDie(testDie));
        assertThrows(IllegalDieException.class,() -> SIXConstraintCell.setDie(testDie));
    }
}