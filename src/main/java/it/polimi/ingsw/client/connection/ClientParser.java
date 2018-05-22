package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.exceptions.GameStartedException;
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

    public static boolean isLoobbyMessage(String command, List<String> parsedResult) throws GameStartedException {
        String [] parsed=Validator.simpleParse(command);

        if(parsed.length==2 && parsed[0].equals("LOBBY")){
            parsedResult.addAll(Arrays.asList(parsed));
            return true;
        }
        if(parsed.length==4 && parsed[0].equals("GAME") && parsed[1].equals("start")){
            throw new GameStartedException(Integer.parseInt(parsed[2]),Integer.parseInt(parsed[3]));
        }
        throw new IllegalArgumentException();
    }

}
