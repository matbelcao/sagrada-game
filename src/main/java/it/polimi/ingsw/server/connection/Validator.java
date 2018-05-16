package it.polimi.ingsw.server.connection;

import java.lang.String;
import java.util.Arrays;
import java.util.List;

public class Validator {
    public static boolean isValid(String command, List<String> parsedParams) {
        String[] temp;
        String keyword;
        if (command == null) {
            return false;
        }

        temp= command.trim().split("\\s+", 2);
        keyword = temp[0];


        switch (keyword) {

            case "LOGIN":

                return checkLoginParams(command, parsedParams);


            case "GET":


            case "GET_DICE_LIST":
                break;

            case "SELECT":
                break;

            case "DISCARD":
                break;

            case "CHOOSE":
                break;

            case "QUIT":
                break;

            case "ACK":
                break;
            default:
                return false;


        }
        return false;
    }

    public static boolean isValidUsername(String username){
        return username.split("[^a-zA-Z0-9]").length==1 && username.length()>0;
    }
    /**
     * This method checks if the login parametersa are valid
     * @param command the unparsed string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    private static boolean checkLoginParams(String command, List<String> parsedResult){
        if(parsedResult!=null){ parsedResult.clear(); }

        String [] temp=command.trim().split("\\s+");
        if(temp.length!=3||!isValidUsername(temp[1])){
            return false;
        }

        parsedResult.addAll(Arrays.asList(temp));
        return true;
    }

}





















