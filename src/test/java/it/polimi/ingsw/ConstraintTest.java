package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstraintTest {
    @Test
    public void  colorConstraint(){
        Constraint test = new Constraint("RED");
        assertEquals(test.getColor().toString(),"RED");
        assertEquals(test.getShade(),null);
        assertEquals(test.isColorConstraint(),Boolean.TRUE);
        assertEquals("\u001B[31m\u25a0\u001B[0m",test.toUtf());
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
    public void isActive(){
        Constraint test = new Constraint("ONE");
        assertEquals(Boolean.TRUE,test.isActive());
        test.setActive(false);
        assertEquals(Boolean.FALSE,test.isActive());
    }
}
