package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchemaCardTest {
    @Test
    void testSchemaCardConstructor(){
        SchemaCard schema1 = new SchemaCard(1,"src\\xml\\SchemaCard.xml");
        assertEquals("Kaleidoscopic Dream",schema1.getName());
        assertEquals(Integer.parseInt("1"),schema1.getId());
        assertEquals(Integer.parseInt("4"),schema1.getFavorTokens());

        //Check if constraints are correctly initialized
        assertEquals(Boolean.TRUE,schema1.getCell(0, 0).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(0, 1).hasConstraint());
        assertEquals(Boolean.FALSE,schema1.getCell(0, 2).hasConstraint());
        assertEquals(Boolean.FALSE,schema1.getCell(0, 3).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(0, 4).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(1, 0).hasConstraint());
        assertEquals(Boolean.FALSE,schema1.getCell(1, 1).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(1, 2).hasConstraint());
        assertEquals(Boolean.FALSE,schema1.getCell(1, 3).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(1, 4).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(2, 0).hasConstraint());
        assertEquals(Boolean.FALSE,schema1.getCell(2, 1).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(2, 2).hasConstraint());
        assertEquals(Boolean.FALSE,schema1.getCell(2, 3).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(2, 4).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(3, 0).hasConstraint());
        assertEquals(Boolean.FALSE,schema1.getCell(3, 1).hasConstraint());
        assertEquals(Boolean.FALSE,schema1.getCell(3, 2).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(3, 3).hasConstraint());
        assertEquals(Boolean.TRUE,schema1.getCell(3, 4).hasConstraint());


        // Check if all the Constraints are correctly placed in the SchemaCard 1
        assertEquals("YELLOW",schema1.getCell(0, 0).getConstraint().toString());
        assertEquals("BLUE",schema1.getCell(0, 1).getConstraint().toString());
        assertEquals("ONE",schema1.getCell(0, 4).getConstraint().toString());
        assertEquals("GREEN",schema1.getCell(1, 0).getConstraint().toString());
        assertEquals("FIVE",schema1.getCell(1, 2).getConstraint().toString());
        assertEquals("FOUR",schema1.getCell(1, 4).getConstraint().toString());
        assertEquals("THREE",schema1.getCell(2, 0).getConstraint().toString());
        assertEquals("RED",schema1.getCell(2, 2).getConstraint().toString());
        assertEquals("GREEN",schema1.getCell(2, 4).getConstraint().toString());
        assertEquals("TWO",schema1.getCell(3, 0).getConstraint().toString());;
        assertEquals("BLUE",schema1.getCell(3, 3).getConstraint().toString());
        assertEquals("YELLOW",schema1.getCell(3, 4).getConstraint().toString());

        SchemaCard schema2 = new SchemaCard(24,"src\\xml\\SchemaCard.xml");
        assertEquals("Industria",schema2.getName());
        assertEquals(Integer.parseInt("24"),schema2.getId());
        assertEquals(Integer.parseInt("5"),schema2.getFavorTokens());
    }
}