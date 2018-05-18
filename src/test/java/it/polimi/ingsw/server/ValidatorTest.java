package it.polimi.ingsw.server;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertTrue(!Validator.isValid("   LOGIN    ,,,MR   RM  ",parsedResult));

        //testing invalid login
        assertTrue(!Validator.isValid("   LOGIN    MR     ",parsedResult));
        assertTrue(parsedResult.isEmpty());
        assertTrue(!Validator.isValid("   LOGIN    , ,,MR     ",parsedResult));
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

        //invalid playerid
        command= "   GET   schema  4  ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkGetParams(command,parsedResult));

        //invalid username

        assertTrue(!Validator.isValid("    GET  4   ",parsedResult));


    }
    @Test
    public void testCheckSelect(){

    }

    @Test
    public void testIsValidUsername(){
        assertTrue(Validator.isValidUsername("luca"));
        assertTrue(!Validator.isValidUsername("luca.ssd"));
        assertTrue(!Validator.isValidUsername("...luca"));
        assertTrue(!Validator.isValidUsername("l uca"));
    }
}
