package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchemaCardTest {
    @Test
    void testSchemaCardConstructor(){
        SchemaCard schema1 = new SchemaCard(1,"src\\xml\\SchemaCard.xml");
        assertEquals(schema1.getName(),"Kaleidoscopic Dream");
        assertEquals(schema1.getId(),Integer.parseInt("1"));
        assertEquals(schema1.getFavorTokens(),Integer.parseInt("4"));

        // Check if all the Constraints are correctly placed in the SchemaCard 1
        assertEquals(schema1.getCell(0, 0).getConstraint().toString(),"YELLOW");
        assertEquals(schema1.getCell(0, 1).getConstraint().toString(),"BLUE");
        assertEquals(schema1.getCell(0, 2).getConstraint().toString(),"");
        assertEquals(schema1.getCell(0, 3).getConstraint().toString(),"");
        assertEquals(schema1.getCell(0, 4).getConstraint().toString(),"ONE");
        assertEquals(schema1.getCell(1, 0).getConstraint().toString(),"GREEN");
        assertEquals(schema1.getCell(1, 1).getConstraint().toString(),"");
        assertEquals(schema1.getCell(1, 2).getConstraint().toString(),"FIVE");
        assertEquals(schema1.getCell(1, 3).getConstraint().toString(),"");
        assertEquals(schema1.getCell(1, 4).getConstraint().toString(),"FOUR");
        assertEquals(schema1.getCell(2, 0).getConstraint().toString(),"THREE");
        assertEquals(schema1.getCell(2, 1).getConstraint().toString(),"");
        assertEquals(schema1.getCell(2, 2).getConstraint().toString(),"RED");
        assertEquals(schema1.getCell(2, 3).getConstraint().toString(),"");
        assertEquals(schema1.getCell(2, 4).getConstraint().toString(),"GREEN");
        assertEquals(schema1.getCell(3, 0).getConstraint().toString(),"TWO");
        assertEquals(schema1.getCell(3, 1).getConstraint().toString(),"");
        assertEquals(schema1.getCell(3, 2).getConstraint().toString(),"");
        assertEquals(schema1.getCell(3, 3).getConstraint().toString(),"BLUE");
        assertEquals(schema1.getCell(3, 4).getConstraint().toString(),"YELLOW");

        SchemaCard schema2 = new SchemaCard(24,"src\\xml\\SchemaCard.xml");
        assertEquals(schema2.getName(),"Industria");
        assertEquals(schema2.getId(),Integer.parseInt("24"));
        assertEquals(schema2.getFavorTokens(),Integer.parseInt("5"));
    }
}