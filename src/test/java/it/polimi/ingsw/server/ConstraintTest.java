package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.server.model.Constraint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * This test class checks the correct application of a color / face constraint to a die
 */
class ConstraintTest {

    /**
     * Tests the color constraint constructor
     */
    @Test
    void  colorConstraint(){
        Constraint test = new Constraint("RED");
        assertEquals(test.getColor().toString(),"RED");
        assertNull(test.getShade());
        assertEquals(test.isColorConstraint(),Boolean.TRUE);
        assertEquals("\u001B[91m\u25a0\u001B[0m",test.toUtf());
        assertEquals("RED",test.toString());
    }

    /**
     * Tests the shade constraint constructor
     */
    @Test
    void  shadeConstraint(){
        Constraint test = new Constraint("ONE");
        assertNull(test.getColor());
        assertEquals("ONE",test.getShade().toString());
        assertEquals(Boolean.FALSE,test.isColorConstraint());
        assertEquals("\u001B[0m\u2680",test.toUtf());
        assertEquals("ONE",test.toString());
    }

    /**
     * Another test of the constructors
     */
    @Test
    void testConstructors(){
        Constraint shadeConstraint=new Constraint(Shade.valueOf("SIX"));
        assertEquals("SIX",shadeConstraint.getShade().toString());

        Constraint colorConstraint=new Constraint(DieColor.valueOf("RED"));
        assertEquals("RED",colorConstraint.getColor().toString());
    }

}
