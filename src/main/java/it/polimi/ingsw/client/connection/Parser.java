package it.polimi.ingsw.client.connection;

import java.util.Arrays;
import java.util.List;

public class Parser {
    public static boolean parse(String command, List<String> parsedResult) {
        if (command == null) {
            throw new IllegalArgumentException();
        }
        String[] temp;
        String keyword;

        if (parsedResult == null) {
            throw new IllegalArgumentException();
        }

        temp = command.trim().split("\\s+", 2);
        keyword = temp[0];


        switch (keyword) {

            case "Connection established!":

                return checkWelcome(command, parsedResult);
            case "LOGIN":

                return checkLoginParams(command, parsedResult);
            default:
                return false;
        }
    }

    public static boolean checkWelcome(String  rawCommand, List<String> parsedResult){
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("Connection established!")){ return false; }
        if(command.length!=1){
            return false;
        }

        parsedResult.addAll(Arrays.asList(command));
        return true;
    }

    public static boolean checkLoginParams(String  rawCommand, List<String> parsedResult){
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("LOGIN")){ return false; }
        if(command.length!=2 && (!command[1].equals("ok")||!command[1].equals("ko"))){
            return false;
        }

        parsedResult.addAll(Arrays.asList(command));
        return true;
    }

}
