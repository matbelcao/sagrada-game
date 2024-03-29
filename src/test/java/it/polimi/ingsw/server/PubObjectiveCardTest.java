package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.PubObjectiveCard;
import it.polimi.ingsw.server.model.SchemaCard;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This test checks the instantiation and the score algorithm for the public objective cards
 */
class PubObjectiveCardTest {

    /**
     * Checks the correct instantiation from the xml file
     */
    @Test
    void  testPubObjectiveCardConstructor(){
        PubObjectiveCard pub1 = new PubObjectiveCard(1,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        assertEquals(Integer.parseInt("1"),pub1.getId());
        assertEquals("Colori diversi - Riga [6]",pub1.getName());

        assertEquals("Righe senza colori ripetuti",pub1.getDescription());

        PubObjectiveCard pub2 = new PubObjectiveCard(10,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        assertEquals(Integer.parseInt("10"),pub2.getId());
        assertEquals("Varietà di Colore [4]",pub2.getName());

        assertEquals("Set di dadi di ogni colore ovunque",pub2.getDescription());
    }

    /**
     * Tests the score calculating algorithms for all the cards (with all the cell full)
     */
    @Test
    void testGetCardScore1() {
        //Aurorae Magnificus schema
        SchemaCard schema = SchemaCard.getNewSchema(11,false);

        //PublicObjective cards
        PubObjectiveCard pub1 = new PubObjectiveCard(1,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub2 = new PubObjectiveCard(2,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub3 = new PubObjectiveCard(3,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub4 = new PubObjectiveCard(4,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub5 = new PubObjectiveCard(5,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub6 = new PubObjectiveCard(6,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub7 = new PubObjectiveCard(7,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub8 = new PubObjectiveCard(8,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub9 = new PubObjectiveCard(9,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub10 = new PubObjectiveCard(10,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");

        //SchemaCard's dice
        Die[] die = new Die[20];
        die[0] = new Die("FIVE", "RED");
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
        die[11] = new Die("THREE", "PURPLE");
        die[12] = new Die("SIX", "GREEN");
        die[13] = new Die("ONE", "RED");
        die[14] = new Die("THREE", "PURPLE");
        die[15] = new Die("ONE", "BLUE");
        die[16] = new Die("SIX", "YELLOW");
        die[17] = new Die("THREE", "PURPLE");
        die[18] = new Die("TWO", "GREEN");
        die[19] = new Die("FOUR", "RED");

        //legal placements for schema1 (no cells empty)
        try {
            for (int i = 0; i < 20; i++) {
                schema.putDie(i, die[i]);
            }

        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        assertEquals(6,pub1.getCardScore(schema));
        assertEquals(20,pub2.getCardScore(schema));
        assertEquals(5,pub3.getCardScore(schema));
        assertEquals(16,pub4.getCardScore(schema));
        assertEquals(6,pub5.getCardScore(schema));
        assertEquals(6,pub6.getCardScore(schema));
        assertEquals(4,pub7.getCardScore(schema));
        assertEquals(10,pub8.getCardScore(schema));
        assertEquals(15,pub9.getCardScore(schema));
        assertEquals(12,pub10.getCardScore(schema));
    }

    /**
     * Tests the score calculating algorithms for all the cards (with some cells empty)
     */
    @Test
    void testGetCardScore2() {
        //Aurorae Magnificus schema
        SchemaCard schema = SchemaCard.getNewSchema(11,false);

        //PublicObjective cards
        PubObjectiveCard pub1 = new PubObjectiveCard(1,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub2 = new PubObjectiveCard(2,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub3 = new PubObjectiveCard(3,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub4 = new PubObjectiveCard(4,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub5 = new PubObjectiveCard(5,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub6 = new PubObjectiveCard(6,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub7 = new PubObjectiveCard(7,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub8 = new PubObjectiveCard(8,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub9 = new PubObjectiveCard(9,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");
        PubObjectiveCard pub10 = new PubObjectiveCard(10,MasterServer.XML_SOURCE+"PubObjectiveCard.xml");

        //SchemaCard's dice
        Die[] die = new Die[20];
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
                }
            }

        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        assertEquals(6,pub1.getCardScore(schema));
        assertEquals(10,pub2.getCardScore(schema));
        assertEquals(5,pub3.getCardScore(schema));
        assertEquals(8,pub4.getCardScore(schema));
        assertEquals(6,pub5.getCardScore(schema));
        assertEquals(6,pub6.getCardScore(schema));
        assertEquals(2,pub7.getCardScore(schema));
        assertEquals(5,pub8.getCardScore(schema));
        assertEquals(10,pub9.getCardScore(schema));
        assertEquals(8,pub10.getCardScore(schema));
    }
}
