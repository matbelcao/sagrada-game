package it.polimi.ingsw.server;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;
import it.polimi.ingsw.server.model.SchemaCard;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

/**
 * This test class performs tests on the execution flow of the cell iterator used by the schema card
 */
class FullCellIteratorTest {
    private static SchemaCard schema;
    private static FullCellIterator iter;
    private static Die[] die;

    /**
     * Checks the correct instantiation of the iterator, then operation with and without the color constraint
     */
    @BeforeAll
    static void setUp(){
        //Aurorae Magnificus schema
        schema = SchemaCard.getNewSchema(11,false);
        iter= (FullCellIterator) schema.iterator();
        int num=0;
        //SchemaCard's dice
        die = new Die[20];
        die[1] = new Die("TWO", "GREEN");
        die[2] = new Die("ONE", "BLUE");
        die[3] = new Die("THREE", "PURPLE");
        die[4] = new Die("TWO", "BLUE");
        die[5] = new Die("FOUR", "PURPLE");
        die[6] = new Die("SIX", "BLUE");
        die[7] = new Die("FOUR", "PURPLE");
        die[8] = new Die("FIVE", "BLUE");
        die[9] = new Die("SIX", "YELLOW");
        die[10] = new Die("SIX", "YELLOW");
        die[13] = new Die("ONE", "RED");
        die[14] = new Die("THREE", "PURPLE");
        die[15] = new Die("ONE", "BLUE");
        die[16] = new Die("SIX", "YELLOW");
        die[17] = new Die("THREE", "PURPLE");
        die[18] = new Die("TWO", "GREEN");
        die[19] = new Die("FOUR", "RED");

        //legal placements for schema 1 (some cell empty)
        try {
            for (int i = 1; i < 20; i++) {
                if (i!=11 && i!=12) {
                    schema.putDie(i, die[i]);
                    num++;
                    assertEquals(num,iter.numOfDice());
                }
            }

        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        assertThrows(UnsupportedOperationException.class,() -> iter.remove());
    }

    @Test
    void testNumOfDice (){
        assertEquals(17,iter.numOfDice());

        Executable codeToTest=() ->{schema.getCell(15).setDie(null);};
        assertThrows(IllegalArgumentException.class,codeToTest);

        schema.removeDie(15);

        codeToTest= () -> schema.removeDie(0);
        assertThrows(NoSuchElementException.class,codeToTest);


        assertEquals(16,iter.numOfDice());

        try {
            schema.putDie(15,die[15]);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        assertEquals(17,iter.numOfDice());
    }


}
