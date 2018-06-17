package it.polimi.ingsw.server;

import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.PrivObjectiveCard;
import it.polimi.ingsw.server.model.SchemaCard;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class PrivObjectiveCardTest {
    @Test
    void  testPrivObjectiveCardConstructor(){
        PrivObjectiveCard priv1 = new PrivObjectiveCard(1);
        assertEquals(Integer.parseInt("1"),priv1.getId());
        assertEquals("Sfumature Rosse",priv1.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"PrivObjectiveCard"+File.separator+"1.png",priv1.getImgSrc());
        assertEquals("Somma dei valori su tutti i dadi rossi",priv1.getDescription());
        assertEquals("RED",priv1.getColor().toString());

        PrivObjectiveCard priv2 = new PrivObjectiveCard(5);
        assertEquals(Integer.parseInt("5"),priv2.getId());
        assertEquals("Sfumature Viola",priv2.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"PrivObjectiveCard"+File.separator+"5.png",priv2.getImgSrc());
        assertEquals("Somma dei valori su tutti i dadi viola",priv2.getDescription());
        assertEquals("PURPLE",priv2.getColor().toString());
    }

    @Test
    void testGetCardScore(){
        SchemaCard schema1 = new SchemaCard(1);

        //PrivateObjective cards
        PrivObjectiveCard priv1 = new PrivObjectiveCard(1);
        PrivObjectiveCard priv2 = new PrivObjectiveCard(2);
        PrivObjectiveCard priv3 = new PrivObjectiveCard(3);
        PrivObjectiveCard priv4 = new PrivObjectiveCard(4);
        PrivObjectiveCard priv5 = new PrivObjectiveCard(5);

        //SchemaCard's dice
        Die die1= new Die("FOUR","RED");
        Die die2= new Die("ONE","YELLOW");
        Die die3= new Die("FOUR","RED");
        Die die4= new Die("FIVE","GREEN");
        Die die5= new Die("FOUR","YELLOW");
        Die die6= new Die("THREE","PURPLE");
        Die die7= new Die("ONE","BLUE");
        Die die8= new Die("FIVE","RED");
        Die die9= new Die("THREE","GREEN");
        Die die10= new Die("SIX","PURPLE");
        Die die11= new Die("TWO","RED");
        Die die12= new Die("ONE","RED");

        //legal placements
        try {
            schema1.putDie(9,die1);
            schema1.putDie(4,die2);
            schema1.putDie(3,die3);
            schema1.putDie(14,die4);
            schema1.putDie(19,die5);
            schema1.putDie(13,die6);
            schema1.putDie(18,die7);
            schema1.putDie(12,die8);
            schema1.putDie(17,die9);
            schema1.putDie(16,die10);
            schema1.putDie(15,die11);
            schema1.putDie(6,die12);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        //Check scores
        assertEquals(16,priv1.getCardScore(schema1));
        assertEquals(5,priv2.getCardScore(schema1));
        assertEquals(8,priv3.getCardScore(schema1));
        assertEquals(1,priv4.getCardScore(schema1));
        assertEquals(9,priv5.getCardScore(schema1));
    }
}