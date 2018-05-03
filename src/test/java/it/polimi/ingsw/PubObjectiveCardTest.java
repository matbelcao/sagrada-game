package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class PubObjectiveCardTest {
    @Test
    void  testPubObjectiveCardConstructor(){
        PubObjectiveCard pub1 = new PubObjectiveCard(1,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        assertEquals(Integer.parseInt("1"),pub1.getId());
        assertEquals("Colori diversi - Riga [6]",pub1.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"PubObjectiveCard"+File.separator+"1.png",pub1.getImgSrc());
        assertEquals("Righe senza colori ripetuti",pub1.getDescription());

        PubObjectiveCard pub2 = new PubObjectiveCard(10,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        assertEquals(Integer.parseInt("10"),pub2.getId());
        assertEquals("Varietà di Colore [4]",pub2.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"PubObjectiveCard"+File.separator+"10.png",pub2.getImgSrc());
        assertEquals("Set di dadi di ogni colore ovunque",pub2.getDescription());
    }

    @Test
    void testGetCardScore() {
        //Aurorae Magnificus schema
        SchemaCard schema = new SchemaCard(11, "src" + File.separator + "xml" + File.separator + "SchemaCard.xml");

        //PublicObjective cards
        PubObjectiveCard pub1 = new PubObjectiveCard(1,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub2 = new PubObjectiveCard(2,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub3 = new PubObjectiveCard(3,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub4 = new PubObjectiveCard(4,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub5 = new PubObjectiveCard(5,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub6 = new PubObjectiveCard(6,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub7 = new PubObjectiveCard(7,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub8 = new PubObjectiveCard(8,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub9 = new PubObjectiveCard(9,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub10 = new PubObjectiveCard(10,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");

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

        //legal placements
        try {
            for (int i = 0; i < 20; i++) {
                schema.putDie(i, die[i]);
            }

        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        assertEquals(6,pub1.getCardScore(schema));
        assertEquals(5,pub3.getCardScore(schema));
        assertEquals(16,pub4.getCardScore(schema));
        assertEquals(6,pub5.getCardScore(schema));
        assertEquals(6,pub6.getCardScore(schema));
        assertEquals(4,pub7.getCardScore(schema));
        assertEquals(10,pub8.getCardScore(schema));
        assertEquals(15,pub9.getCardScore(schema));
        assertEquals(12,pub10.getCardScore(schema));

    }
}
