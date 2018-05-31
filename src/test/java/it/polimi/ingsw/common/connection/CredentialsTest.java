package it.polimi.ingsw.common.connection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {
    @Test
    void testHash(){
        System.out.println(Credentials.hash("ciccio","password1".toCharArray()));

    }

}