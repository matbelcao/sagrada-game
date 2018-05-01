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
        assertEquals(schema1.getImgSrc(),"src\\img\\SchemaCard\\1.png");
        assertEquals(schema1.getId(),Integer.parseInt("1"));
        //assertEquals(schema1.getFavorTokens(),Integer.parseInt("4"));

        SchemaCard schema2 = new SchemaCard(24,"src\\xml\\SchemaCard.xml");
        assertEquals(schema2.getName(),"Industria");
        assertEquals(schema2.getImgSrc(),"src\\img\\SchemaCard\\24.png");
        assertEquals(schema2.getId(),Integer.parseInt("24"));
        //assertEquals(schema2.getFavorTokens(),Integer.parseInt("5"));
    }
}