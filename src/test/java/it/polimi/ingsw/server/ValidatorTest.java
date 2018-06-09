package it.polimi.ingsw.server;

import it.polimi.ingsw.server.connection.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {
    private static ArrayList<String> parsedResult = new ArrayList<>();
    private static String command;

    @Test
    public void testCheckLogin(){
        String keyword;
        //testing normal valid login with non standard spacing
        assertTrue(Validator.isValid("   LOGIN    MR   RM  ",parsedResult));
        if(Validator.isValid("  LOGIN  MR   RM ",parsedResult)) {
            keyword = parsedResult.get(0);
            assertEquals("LOGIN", keyword);
            assertEquals("MR", parsedResult.get(1));
            assertEquals("RM", parsedResult.get(2));
        }
        //invalid username

        assertFalse(Validator.isValid("   LOGIN    ,,,MR   RM  ",parsedResult));
        assertFalse(Validator.isValid("",parsedResult));
        assertFalse(Validator.isValid("  ",parsedResult));
        assertFalse(Validator.isValid("\n\r",parsedResult));

        //testing invalid login
        assertFalse(Validator.isValid("   LOGIN    MR     ",parsedResult));
        assertTrue(parsedResult.isEmpty());
        assertFalse(Validator.isValid("   LOGIN    , ,,MR     ",parsedResult));
        assertTrue(parsedResult.isEmpty());
    }
    @Test
    public void testCheckGetSchema(){
        String keyword;

        //correct command
        command= "   GET   schema  draft  ";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkGetParams(command,parsedResult));
        if(Validator.isValid(command,parsedResult)) {
            keyword = parsedResult.get(0);
            assertEquals("GET", keyword);
            assertEquals("schema", parsedResult.get(1));
            assertEquals("draft", parsedResult.get(2));
        }

        //misspelled draft
        command= "   GET   schema  dra  ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkGetParams(command,parsedResult));

        //valid playerid
        command= "   GET   schema  3 ";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkGetParams(command,parsedResult));
        if(Validator.isValid(command,parsedResult)) {
            keyword = parsedResult.get(0);
            assertEquals("GET", keyword);
            assertEquals("schema", parsedResult.get(1));
            assertEquals("3", parsedResult.get(2));
        }

        command= "   GET   favor_tokens  3";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkGetParams(command,parsedResult));

        //invalid playerid
        command= "   GET   schema  4  ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkGetParams(command,parsedResult));

        //invalid username

        assertTrue(!Validator.isValid("    GET  4   ",parsedResult));


    }

    @Test
    public void testCheckSelect(){

        command= "          SELECT  9  ";
        assertTrue(Validator.isValid(command,parsedResult));

        command= "          select   die  9  ";

        assertFalse(Validator.isValid(command,parsedResult));
        command= "          se   die  9  ";

        assertFalse(Validator.isValid(command,parsedResult));


        command= "          SEL100  ";
        assertFalse(Validator.isValid(command,parsedResult));
    }

    @Test
    public void testCheckPong(){
        command= "     PONG  ";
        assertTrue(Validator.isValid(command,parsedResult));

        command= "     PONG 1 ";
        assertFalse(Validator.isValid(command,parsedResult));

        command= "    PO ";
        assertFalse(Validator.isValid(command,parsedResult));
    }


    @Test
    public void testCheckChoose() {

        assertTrue(Validator.isValid("GET_DICE_LIST", parsedResult));
        assertEquals("GET_DICE_LIST",parsedResult.get(0));

        //valid
        command = "CHOOSE 6";

        assertTrue(Validator.isValid(command, parsedResult));

        //invalid
        command = "CHOOSE schema 6";

        assertTrue(!Validator.isValid(command, parsedResult));

        //valid schema number (0-3)
        command = "CHOOSE";
        assertFalse(Validator.isValid(command, parsedResult));


        //invalid tool number (0-2)
        command = "CHOOSE t 6";

        assertFalse(Validator.isValid(command, parsedResult));

    }

    @Test
    public void testGetPlacementsList(){
        assertTrue(Validator.isValid("GET_PLACEMENTS_LIST", parsedResult));
        assertEquals("GET_PLACEMENTS_LIST",parsedResult.get(0));
    }

    @Test
    public void testIsValidUsername(){
        assertTrue(Validator.isValidUsername("luca"));
        assertFalse(Validator.isValidUsername("luca.ssd"));
        assertFalse(Validator.isValidUsername("...luca"));
        assertFalse(Validator.isValidUsername("l uca"));
    }
}
