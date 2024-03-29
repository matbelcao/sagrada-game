package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.DraftPool;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

/**
 * This test class checks the draftpool methods
 */
class DraftPoolTest {

    /**
     * Checks the correct instantiation, and the dice extraction/clearing
     */
    @Test
    void testDraftPool(){
        DraftPool draftPool=new DraftPool();
        List<Die> draftedDie=draftPool.draftDice(3);

        assertEquals(7,draftedDie.size());
        draftPool.removeDie(4);
        assertEquals(6,draftedDie.size());

        List<Die> diceList1=draftPool.getDraftedDice();
        List<Die> diceList2=draftPool.getDraftedDice();
        Die d1=diceList1.get(4);
        Die d2=draftPool.getDraftedDice().get(4);
        assertEquals(4,diceList2.indexOf(d1));
        assertEquals(d2,d1);

        draftPool.removeDie(2);
        d2=draftPool.getDraftedDice().get(4);
        assertNotEquals(d2,d1);
        assertEquals(3,draftPool.getDraftedDice().indexOf(d1));

        assertNotNull(draftPool.getRoundTrack());
        draftPool.clearDraftPool(0);
        assertEquals(0,draftPool.getDraftedDice().size());

        Die die=new Die("ONE","NONE");
        assertNotEquals(die.getColor().toString(),draftPool.putInBagAndExtract(die));
    }
}
