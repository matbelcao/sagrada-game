package it.polimi.ingsw.common.connection;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the login information and the hashing function
 */
class CredentialsTest {

    /**
     * Checks the hashing function
     */
    @Test
    void testHash(){
        System.out.println("1:"+ Arrays.toString(Credentials.hash("ciccio","password1".toCharArray())));
        System.out.println("2:"+Credentials.toString(Credentials.hash("ciccio","password2".toCharArray())));
        System.out.println("3:"+Credentials.toString(Credentials.hash("ciccio  ","".toCharArray())));
        System.out.println("4:"+Credentials.toString(Credentials.hash("cicco ","".toCharArray())));
    }

}