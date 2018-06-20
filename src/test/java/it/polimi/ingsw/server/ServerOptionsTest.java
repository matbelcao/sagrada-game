package it.polimi.ingsw.server;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class performs general tests of the command line configurations
 */
class ServerOptionsTest {
    List<String> result=new ArrayList<>();

    /**
     * here we test some valid combinations of options for the server
     */
    @Test
    void testValidOptions(){
        assertTrue(ServerOptions.getOptions("-A ".trim().split("\\s+"),result));

        assertTrue(ServerOptions.getOptions("-Aa 127.0.0.1 -l 33".trim().split("\\s+"),result));

        assertTrue(ServerOptions.getOptions("-l 129 -t 222 -a 12.21.2.12".trim().split("\\s+"),result));
    }

    @Test
    void testInvalidOptions(){
        //option that requires a parameter is not place at the end of a group of shortened options
        assertFalse(ServerOptions.getOptions("-aA ".trim().split("\\s+"),result));
        //you need to specify parameters for options that require that
        assertFalse(ServerOptions.getOptions("-a ".trim().split("\\s+"),result));
        //invalid ip address
        assertFalse(ServerOptions.getOptions("-Aa 127.0.1 -l 33".trim().split("\\s+"),result));
        //parameters for options need to be separeted from their option with at least one space
        assertFalse(ServerOptions.getOptions(" -t222 ".trim().split("\\s+"),result));
        //you can't have repeated options
        assertFalse(ServerOptions.getOptions("-t 43 --turn-time 333 ".trim().split("\\s+"),result));
        assertFalse(ServerOptions.getOptions(" -A -A ".trim().split("\\s+"),result));
    }



}