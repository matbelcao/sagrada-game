package it.polimi.ingsw.server;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatorTest {
    private static ArrayList<String> parsedResult = new ArrayList<>();

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
    public void testIsValidUsername(){
        assertTrue(Validator.isValidUsername("luca"));
        assertTrue(!Validator.isValidUsername("luca.ssd"));
        assertTrue(!Validator.isValidUsername("...luca"));
        assertTrue(!Validator.isValidUsername("l uca"));
    }
}
