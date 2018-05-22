package it.polimi.ingsw.client;

import it.polimi.ingsw.server.connection.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ClientOptionsTest {
    private static String args;
    private static ArrayList<String> options=new ArrayList<>();
    @Test
    public void testArgs(){

        args="   -h      ";
        assertTrue(ClientOptions.getOptions(Validator.simpleParse(args)).contains("h"));

        args=" --socket   -ga  192.168.1.1";
        options= (ArrayList<String>) ClientOptions.getOptions(Validator.simpleParse(args));

        assertEquals("s",options.get(0));
        assertEquals("g",options.get(1));
        assertEquals("a",options.get(2));
        assertEquals("192.168.1.1",options.get(3));

        args=" -g --gui";
        assertThrows(IllegalArgumentException.class,()-> ClientOptions.getOptions(Validator.simpleParse(args)));
        args=" --gui -c";
        assertThrows(IllegalArgumentException.class,()-> ClientOptions.getOptions(Validator.simpleParse(args)));

        args=" --rmi --socket ";
        assertThrows(IllegalArgumentException.class,()-> ClientOptions.getOptions(Validator.simpleParse(args)));

        args=" -sg --socket ";
        assertThrows(IllegalArgumentException.class,()-> ClientOptions.getOptions(Validator.simpleParse(args)));

        args=" -a --gui";
        assertThrows(IllegalArgumentException.class,()-> ClientOptions.getOptions(Validator.simpleParse(args)));


        args=" -r -g -a 192.168.1.2";
        String args2=" -rga 192.168.1.2";
        assertEquals(ClientOptions.getOptions(Validator.simpleParse(args)),ClientOptions.getOptions(Validator.simpleParse(args2)));



    }
}
