package it.polimi.ingsw.server;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.connection.MasterServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RMIConnectionTest {
    private static MasterServer server=MasterServer.getMasterServer();
    @BeforeEach
    public void beforeEachTest(){
        server.startRMI();
        //MasterServer.getMasterServer().startSocket();
    }
    //This test logs 9 clients and verifies if they get properly saved by the master server
    //@Disabled("too slow")
    @Test
    public void orderedConnection(){
        ArrayList<User> users;
        Client c1 = new Client("CONSOLE","RMI");
        Client c2 = new Client("CONSOLE","RMI");
        Client c3 = new Client("CONSOLE","RMI");
        Client c4 = new Client("CONSOLE","RMI");
        Client c5 = new Client("CONSOLE","RMI");
        Client c6 = new Client("CONSOLE","RMI");
        Client c7 = new Client("CONSOLE","RMI");
        Client c8 = new Client("CONSOLE","RMI");
        Client c9 = new Client("CONSOLE","RMI");
        c1.setUsername("a");
        c2.setUsername("b");
        c3.setUsername("c");
        c4.setUsername("d");
        c5.setUsername("e");
        c6.setUsername("f");
        c7.setUsername("g");
        c8.setUsername("h");
        c9.setUsername("i");
        c1.setPassword("1");
        c2.setPassword("2");
        c3.setPassword("3");
        c4.setPassword("4");
        c5.setPassword("5");
        c6.setPassword("6");
        c7.setPassword("7");
        c8.setPassword("8");
        c9.setPassword("9");

        assertEquals(0,server.getUsersSize());
        //connection of three clients
        c1.setupConnection();
        assertTrue(server.getUsersSize()== 1);
        assertEquals(c1.getUsername(),MasterServer.getMasterServer().getUser(c1.getUsername()).getUsername());
        assertEquals("1",MasterServer.getMasterServer().getUser(c1.getUsername()).getPassword());

        c2.setupConnection();
        assertTrue(server.getUsersSize()== 2);
        assertEquals(c2.getUsername(),MasterServer.getMasterServer().getUser(c2.getUsername()).getUsername());
        assertEquals("2",MasterServer.getMasterServer().getUser(c2.getUsername()).getPassword());

        c3.setupConnection();
        assertTrue(server.getUsersSize()== 3);
        assertEquals(c3.getUsername(),MasterServer.getMasterServer().getUser(c3.getUsername()).getUsername());
        assertEquals("3",MasterServer.getMasterServer().getUser(c3.getUsername()).getPassword());

        //connection of a client already logged: no user added
        c3.setupConnection();
        assertTrue(server.getUsersSize()== 3);
        assertEquals(c1.getUsername(),MasterServer.getMasterServer().getUser(c1.getUsername()).getUsername());
        assertEquals(c2.getUsername(),MasterServer.getMasterServer().getUser(c2.getUsername()).getUsername());
        assertEquals(c3.getUsername(),MasterServer.getMasterServer().getUser(c3.getUsername()).getUsername());

        //connection of a client with wrong password TO DO

        c4.setupConnection();
        assertTrue(server.getUsersSize()== 4);
        c5.setupConnection();
        assertTrue(server.getUsersSize()== 5);
        c6.setupConnection();
        assertTrue(server.getUsersSize()== 6);
        c7.setupConnection();
        assertTrue(server.getUsersSize()== 7);
        c8.setupConnection();
        assertTrue(server.getUsersSize()== 8);
        c9.setupConnection();
        assertTrue(server.getUsersSize()== 9);
    }
}
