package it.polimi.ingsw.common.connection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {
    @Test
    void testHash(){
        System.out.println("1:"+new String(Credentials.hash("ciccio","password1".toCharArray())));
        System.out.println("2:"+new String(Credentials.hash("ciccio","password2".toCharArray())));
        System.out.println("3:"+new String(Credentials.hash("ciccio","p4ssword1".toCharArray())));
        System.out.println("4:"+new String(Credentials.hash("ciccio","passw0rd1".toCharArray())));
    }

}