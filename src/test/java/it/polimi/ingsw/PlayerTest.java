package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void testPlayerConstructor(){
        Player player1= new Player("Mario","password1");

        assertEquals("Mario",player1.getUsername());
        assertEquals(0,player1.getFavorTokens());
    }

    @Test
    void testLogin(){
        Player player1= new Player("Mario","password1");
        Player player2= new Player("Luigi","password2");

        assertEquals(true,player1.login("Mario","password1"));
        assertEquals(false,player1.login("Mario","password2"));
        assertEquals(false,player1.login("Luigi","password1"));
    }

    @Test
    void testChooseSchemaCard(){
        //to be continued
        /*Player player1= new Player("Mario","password1");
        player1.chooseSchemaCard(2,);*/
    }

    @Test
    void testdecreaseFavorTokens(){
        Player player= new Player("Mario","password1");
        player.chooseSchemaCard(2);

        player.decreaseFavorTokens(1);
        assertEquals(4,player.getFavorTokens());

        player.decreaseFavorTokens(2);
        assertEquals(2,player.getFavorTokens());

        assertEquals(false,player.decreaseFavorTokens(6));
    }

    @Test
    void testCalculateScore(){
        Player player= new Player("Mario","password1");

        //Aurorae Magnificus schema
        player.chooseSchemaCard(11);

        //PublicObjective cards
        PubObjectiveCard pub2 = new PubObjectiveCard(2,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub8 = new PubObjectiveCard(8,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        PubObjectiveCard pub9 = new PubObjectiveCard(9,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");

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
        /*try {
            for (int i = 1; i < 20; i++) {
                if (i!=11 && i!=12) {
                    player.  .putDie(i, die[i]);
                }
            }

        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        assertEquals(10,pub2.getCardScore(schema));
        assertEquals(5,pub8.getCardScore(schema));
        assertEquals(10,pub9.getCardScore(schema));*/
    }
}
