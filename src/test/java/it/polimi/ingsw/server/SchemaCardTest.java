package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.SchemaCard;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Loading from file and initialization of attributes
 * Correct assignment of constraints
 * Possible placings of a valid die
 * Positioning invalid first die on non-board cell
 * Valid placement of the first die
 * Invalid placement of the same die in the same cell
 * Invalid placement of a die in a cell not adjacent to other dice
 * Invalid placement of a die in a busy cell
 * Invalid placement of a die in a cell with color restriction
 * Invalid placement of a die in a cell with face restriction
 * Valid placement in a cell with color restrictions
 * Valid placement in a cell with face restrictions
 */
class SchemaCardTest {
    /**
     * Tests the correct instantiation of the card
     */
    @Test
    void testSchemaCardConstructor(){
        SchemaCard schema1 = SchemaCard.getNewSchema(1,false);
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

        SchemaCard schema2 = SchemaCard.getNewSchema(24,false);
        assertEquals("Industria",schema2.getName());
        assertEquals(Integer.parseInt("24"),schema2.getId());
        assertEquals(Integer.parseInt("5"),schema2.getFavorTokens());
    }

    /**
     * Tests the list of possible placements with the adjacency constraint, for the tool #9
     */
    @Test
    void testNonAdjacentPlacement(){
        SchemaCard schema1 = SchemaCard.getNewSchema(1,false);
        ArrayList list= (ArrayList) schema1.listPossiblePlacements(new Die("FOUR","GREEN"),IgnoredConstraint.ADJACENCY);
        assertEquals(11,list.size());
        try {
            schema1.putDie(3,new Die("THREE","RED"));
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }
        list= (ArrayList) schema1.listPossiblePlacements(new Die("FOUR","GREEN"),IgnoredConstraint.ADJACENCY);
        assertEquals(10,list.size());

       assertThrows(IllegalDieException.class,() -> schema1.putDie(13,new Die("FOUR","GREEN")));
        try {
            schema1.putDie(13,new Die("FOUR","GREEN"),IgnoredConstraint.ADJACENCY);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        list= (ArrayList) schema1.listPossiblePlacements(new Die("FOUR","GREEN"),IgnoredConstraint.ADJACENCY);
        assertEquals(7,list.size());


    }

    /**
     * Tests the list of possible placements with the swap option enabled
     */
    @Test
    void testDiePlacement(){
        SchemaCard schema1 = SchemaCard.getNewSchema(1,false);
        Die die1= new Die("FOUR","RED");
        Die die2= new Die("TWO","GREEN");
        Die die3= new Die("FOUR","RED");
        Die die4= new Die("ONE","YELLOW");
        ArrayList <Integer> list = null;


        list= (ArrayList<Integer>) schema1.listPossiblePlacements(die1);

        assertEquals(5,list.size());

    //illegal placement of a die
        Executable codeToTest = () -> schema1.putDie(8,die1);
        Assertions.assertThrows(IllegalDieException.class, codeToTest);


    //legal placement
        try {
            schema1.putDie(9,die1);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }
        assertEquals(die1,schema1.getCell(9).getDie());

    //illegal placement of die (non adjacent cell)
        codeToTest= () -> schema1.putDie(2,die3);
        Assertions.assertThrows(IllegalDieException.class, codeToTest);

    //illegal placement of die (a die is already there)
        codeToTest= () -> schema1.putDie(9,die3);
        Assertions.assertThrows(IllegalDieException.class, codeToTest);

    //illegal placement of die (shade constraint not respected)
        codeToTest= () -> schema1.putDie(4,die2);
        Assertions.assertThrows(IllegalDieException.class, codeToTest);

    //illegal placement of die (color constraint not respected)
        codeToTest= () -> schema1.putDie(14,die4);
        Assertions.assertThrows(IllegalDieException.class, codeToTest);

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

    /**
     * Tests that the lists of dice returns the correct updated data
     */
    @Test
    void testListGenerator(){
        SchemaCard schema1 = SchemaCard.getNewSchema(1,false);
        Die die1= new Die("FOUR","RED");
        Die die2= new Die("TWO","GREEN");
        Die die3= new Die("FOUR","RED");
        Die die4= new Die("ONE","YELLOW");

        //legal placement
        try {
            schema1.putDie(9,die1);
            schema1.putDie(3,die3);
            schema1.putDie(4,die4);
            schema1.putDie(8,die2);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        List<Die> schemaContent1=schema1.getSchemaDiceList(DieColor.NONE);
        assertEquals(4,schemaContent1.size());

        assertEquals(die1.toString(),schemaContent1.get(0).toString());
        assertEquals(die4.toString(),schemaContent1.get(1).toString());
        assertEquals(die2.toString(),schemaContent1.get(2).toString());
        assertEquals(die3.toString(),schemaContent1.get(3).toString());


        List<Die> schemaContent2=schema1.getSchemaDiceList(DieColor.RED);
        assertEquals(2,schemaContent2.size());

        assertEquals(die1,schemaContent2.get(1));
        assertEquals(die3,schemaContent2.get(0));
        assertEquals(9,schema1.getDiePosition(die1));
        assertEquals(8,schema1.getDiePosition(die2));

        schema1.removeDie(9);
        List<Die> schemaContent3=schema1.getSchemaDiceList(DieColor.RED);
        assertEquals(1,schemaContent3.size());
        assertEquals(die3.toString(),schemaContent3.get(0).toString());

        schema1.removeDie(3);
        assertEquals(2,schema1.getSchemaDiceList(DieColor.NONE).size());
    }
}