package it.polimi.ingsw.client;

import it.polimi.ingsw.server.controller.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ClientOptionsTest {
    private static String args;
    private static ArrayList<String> options=new ArrayList<>();
    @Test
    public void testArgs(){

        args="   -h      ";
        assertTrue(ClientOptions.getOptions(Validator.simpleParse(args),options));
        assertTrue(options.contains("h"));

        args="   -i     ";
        assertTrue(ClientOptions.getOptions(Validator.simpleParse(args),options));

        args=" --socket   -ga  192.168.1.1";
        assertTrue(ClientOptions.getOptions(Validator.simpleParse(args),options));

        assertEquals("s",options.get(0));
        assertEquals("g",options.get(1));
        assertEquals("a",options.get(2));
        assertEquals("192.168.1.1",options.get(3));

        args=" -g --gui";
        assertTrue(!ClientOptions.getOptions(Validator.simpleParse(args),options));
        args=" --gui -c";
        assertTrue(!ClientOptions.getOptions(Validator.simpleParse(args),options));
        args=" --rmi --socket ";
        assertTrue(!ClientOptions.getOptions(Validator.simpleParse(args),options));
        args=" -sg --socket ";
        assertTrue(!ClientOptions.getOptions(Validator.simpleParse(args),options));
        args=" -a --gui";

        assertTrue(!ClientOptions.getOptions(Validator.simpleParse(args),options));

        ArrayList<String> options2=new ArrayList<>();
        args=" -r -g -a 192.168.1.2";
        String args2=" -rga 192.168.1.2";
        assertTrue(ClientOptions.getOptions(Validator.simpleParse(args),options));
        assertTrue(ClientOptions.getOptions(Validator.simpleParse(args2),options2));
        assertEquals(options2,options);




    }
}
