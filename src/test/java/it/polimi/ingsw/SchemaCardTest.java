package it.polimi.ingsw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SchemaCardTest {
    @Test
    void testSchemaCardConstructor(){
        SchemaCard schema1 = new SchemaCard(1,"src"+ File.separator +"xml"+ File.separator +"SchemaCard.xml");
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

        SchemaCard schema2 = new SchemaCard(24,"src"+ File.separator +"xml"+ File.separator +"SchemaCard.xml");
        assertEquals("Industria",schema2.getName());
        assertEquals(Integer.parseInt("24"),schema2.getId());
        assertEquals(Integer.parseInt("5"),schema2.getFavorTokens());
    }
    @Test
    void testDiePlacement(){
        SchemaCard schema1 = new SchemaCard(1,"src" + File.separator + "xml"+ File.separator +"SchemaCard.xml");
        Die die1= new Die("FOUR","RED");
        Die die2= new Die("TWO","GREEN");
        Die die3= new Die("FOUR","RED");
        Die die4= new Die("ONE","YELLOW");
        ArrayList <Integer> list;

        list= schema1.listPossiblePlacements(die1);
        assertEquals(5,list.size());

    //illegal placement of a die
        Executable codeToTest = () -> schema1.putDie(8,die1);
        Assertions.assertThrows(AssertionError.class, codeToTest);


    //legal placement
        try {
            schema1.putDie(9,die1);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }
        assertEquals(die1,schema1.getCell(9).getDie());

    //legal placement of the same die ->illegal
        codeToTest= () -> schema1.putDie(13,die1);
        Assertions.assertThrows(AssertionError.class, codeToTest);

    //illegal placement of die (non adjacent cell)
        codeToTest= () -> schema1.putDie(2,die3);
        Assertions.assertThrows(AssertionError.class, codeToTest);

    //illegal placement of die (a die is already there)
        codeToTest= () -> schema1.putDie(9,die3);
        Assertions.assertThrows(AssertionError.class, codeToTest);

    //illegal placement of die (shade constraint not respected)
        codeToTest= () -> schema1.putDie(4,die2);
        Assertions.assertThrows(AssertionError.class, codeToTest);

    //illegal placement of die (color constraint not respected)
        codeToTest= () -> schema1.putDie(14,die4);
        Assertions.assertThrows(AssertionError.class, codeToTest);

    //legal placement of an equal die in another position
        try {
            schema1.putDie(3,die3);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }
        assertEquals(die3,schema1.getCell(3).getDie());

    //legal placement of a die  (shade constraint test)
        try {
            schema1.putDie(4,die4);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }
        assertEquals(die4,schema1.getCell(4).getDie());

    //legal placement of a die  (color constraint test)
        try {
            schema1.putDie(8,die2);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }
        assertEquals(die2,schema1.getCell(8).getDie());


    }
}