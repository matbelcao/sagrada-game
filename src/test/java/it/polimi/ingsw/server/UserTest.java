package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.server.controller.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test class performs general tests on the constructor and the getter and setter methods of the User class
 */
class UserTest {

    /**
     * General test of getter and setter
     */
    @Test
    void testConstructor(){
        User user=new User("User1","Password1".toCharArray());

        assertEquals("User1",user.getUsername());
        assertEquals("Password1",new String(user.getPassword()));
        assertEquals(UserStatus.CONNECTED,user.getStatus());

        user.setStatus(UserStatus.DISCONNECTED);
        assertEquals(UserStatus.DISCONNECTED,user.getStatus());

        user.setConnectionMode(ConnectionMode.RMI);
        assertEquals(ConnectionMode.RMI,user.getConnectionMode());
    }
}
