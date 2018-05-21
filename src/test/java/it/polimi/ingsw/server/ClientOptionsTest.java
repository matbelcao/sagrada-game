package it.polimi.ingsw.server;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.connection.Validator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientOptionsTest {
    private static String args;
    private static ArrayList<String> options=new ArrayList<>();
    @Test
    public void testArgs(){

        args="   -h      ";
        assertTrue(Client.getOptions(Validator.simpleParse(args)).contains("h"));

        args=" --socket   -ga  192.168.1.1";
        options= (ArrayList<String>) Client.getOptions(Validator.simpleParse(args));

        assertEquals("s",options.get(0));
        assertEquals("g",options.get(1));
        assertEquals("a",options.get(2));
        assertEquals("192.168.1.1",options.get(3));

        args=" -g --gui";
        assertThrows(IllegalArgumentException.class,()-> Client.getOptions(Validator.simpleParse(args)));
        args=" --gui -c";
        assertThrows(IllegalArgumentException.class,()-> Client.getOptions(Validator.simpleParse(args)));

        args=" --rmi --socket ";
        assertThrows(IllegalArgumentException.class,()-> Client.getOptions(Validator.simpleParse(args)));

        args=" -a --gui";
        assertThrows(IllegalArgumentException.class,()-> Client.getOptions(Validator.simpleParse(args)));


        args=" -r -g -a 192.168.1.2";
        String args2=" -rga 192.168.1.2";
        assertEquals(Client.getOptions(Validator.simpleParse(args)),Client.getOptions(Validator.simpleParse(args2)));



    }
}
