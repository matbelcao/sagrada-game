package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.server.connection.Validator;

import java.util.Arrays;
import java.util.List;

public class ClientParser {

    /**
     * This method is used by the client to check  whether the server responded with a LOGIN ok or a LOGIN ko
     * @param command
     * @return true iff LOGIN ok
     */
    public static boolean isLoginOk(String command) {
        String [] parsed=Validator.simpleParse(command);

        if(parsed[0].equals("LOGIN") ){
            if(parsed[1].equals("ok")){ return true; }
            if(parsed[1].equals("ko")){ return false; }
        }
        throw new IllegalArgumentException();
    }

}
