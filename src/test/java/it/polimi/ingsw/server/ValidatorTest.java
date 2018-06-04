package it.polimi.ingsw.server;

import it.polimi.ingsw.server.connection.Validator;
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
        assertTrue(!Validator.isValid("",parsedResult));
        assertTrue(!Validator.isValid("  ",parsedResult));
        assertTrue(!Validator.isValid("\n\r",parsedResult));

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

        //valid select
        command= "          SELECT   die  9  ";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkSelectParams(command,parsedResult));

        //invalid selects
        command= "          select   die  9  ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkSelectParams(command,parsedResult));

        command= "          se   die  9  ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkSelectParams(command,parsedResult));

        command= "          SELECT   die 100  ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkSelectParams(command,parsedResult));


        command= "          SELECT   die  09  ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkSelectParams(command,parsedResult));

        //valid
        command= "          SELECT   die  90  ";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkSelectParams(command,parsedResult));

        command= "          SELECT   modified_die    ";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkSelectParams(command,parsedResult));

        //invalid
        command= "          SELECT    modified_die 6  ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkSelectParams(command,parsedResult));
    }

    @Test
    public void testCheckAck(){

        //invalid
        command= "     ACK  ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkAckParams(command,parsedResult));
        //valid
        command= "     ACK game ";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkAckParams(command,parsedResult));

        command= "     ACK status ";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkAckParams(command,parsedResult));

        command= "ACK send ";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkAckParams(command,parsedResult));

        command= "     ACK list";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkAckParams(command,parsedResult));

        command= "     ACK game ";

        assertTrue(Validator.isValid(command,parsedResult));
        assertTrue(Validator.checkAckParams(command,parsedResult));


        command= "     ACK games ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkAckParams(command,parsedResult));

        //invalid

        command= "     ACK bar ";

        assertTrue(!Validator.isValid(command,parsedResult));
        assertTrue(!Validator.checkAckParams(command,parsedResult));
    }


    @Test
    public void testCheckChoose() {

        assertTrue(Validator.isValid("GET_DICE_LIST schema", parsedResult));
        assertTrue(Validator.checkGetDiceListParams("GET_DICE_LIST schema", parsedResult));

        //valid
        command = "CHOOSE die_placement 6";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));
        //invalid number
        command = "CHOOSE schema 6";

        assertTrue(!Validator.isValid(command, parsedResult));
        assertTrue(!Validator.checkChooseParams(command, parsedResult));

        //valid schema number (0-3)
        command = "CHOOSE schema 2";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));

        //invalid tool number (0-2)
        command = "CHOOSE tool 6";

        assertTrue(!Validator.isValid(command, parsedResult));
        assertTrue(!Validator.checkChooseParams(command, parsedResult));

        command = "CHOOSE tool 2";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));

        //valid number (1-6)
        command = "CHOOSE face 6";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));

        command = "CHOOSE die 6 increase";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));

        command = "CHOOSE die 6 decrease";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));

        command = "CHOOSE die 6 reroll";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));

        //die chosen from draftpool (0-8)
        command = "CHOOSE die 16 flip";

        assertTrue(!Validator.isValid(command, parsedResult));
        assertTrue(!Validator.checkChooseParams(command, parsedResult));

        command = "CHOOSE die 1 flip";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));


        command = "CHOOSE die 8 put_in_bag";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));

        command = "CHOOSE die 8";

        assertTrue(Validator.isValid(command, parsedResult));
        assertTrue(Validator.checkChooseParams(command, parsedResult));


        //valid


    }

        @Test
    public void testIsValidUsername(){
        assertTrue(Validator.isValidUsername("luca"));
        assertTrue(!Validator.isValidUsername("luca.ssd"));
        assertTrue(!Validator.isValidUsername("...luca"));
        assertTrue(!Validator.isValidUsername("l uca"));
    }
}
