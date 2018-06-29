package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.Shade;
import it.polimi.ingsw.server.model.Constraint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstraintTest {
    @Test
    public void  colorConstraint(){
        Constraint test = new Constraint("RED");
        assertEquals(test.getColor().toString(),"RED");
        assertEquals(test.getShade(),null);
        assertEquals(test.isColorConstraint(),Boolean.TRUE);
        assertEquals("\u001B[91m\u25a0\u001B[0m",test.toUtf());
        assertEquals("RED",test.toString());
        }

    @Test
    public void  shadeConstraint(){
        Constraint test = new Constraint("ONE");
        assertEquals(null, test.getColor());
        assertEquals("ONE",test.getShade().toString());
        assertEquals(Boolean.FALSE,test.isColorConstraint());
        assertEquals("\u001B[0m\u2680",test.toUtf());
        assertEquals("ONE",test.toString());
    }

    @Test
    void testConstructors(){
        Constraint shadeConstraint=new Constraint(Shade.valueOf("SIX"));
        assertEquals("SIX",shadeConstraint.getShade().toString());

        Constraint colorConstraint=new Constraint(DieColor.valueOf("RED"));
        assertEquals("RED",colorConstraint.getColor().toString());
    }

}
