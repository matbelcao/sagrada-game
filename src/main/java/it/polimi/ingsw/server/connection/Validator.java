package it.polimi.ingsw.server.connection;

import java.util.Arrays;
import java.util.List;

/**
 * This class implements various static methods that are used to validate client socket messages, it also offers a simple method to parse commands splitting them on whitespaces
 */
public class Validator {
    static final String ALPHANUMERIC="[0-9a-zA-Z]+";
    static final String BEGINS_WITH_DIGIT="^([0-9]+)[^0-9]*";
    static final String PLAYER_ID="[0-3]";
    static final String TWO_DIGITS_MAX = "[0-9]|([1-9][0-9])";
    static final String CELL_INDEX = "[0-9]|([1][0-9])";
    static final String TOOL_INDEX = "[0-2]";
    static final String DIE_FACE = "[1-6]";
    static final String DRAFT_POOL_DIE = "[0-8]";

    private Validator(){ }

    public static String [] simpleParse(String command){
        return command.trim().split("\\s+");
    }

    /**
     * This method checks if parameters of a generic client-side command are valid
     * @param command the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    public static boolean isValid(String command, List<String> parsedResult) {
        if( command==null){ throw new IllegalArgumentException(); }
        String[] temp;
        String keyword;

        if(parsedResult==null){throw new IllegalArgumentException();}

        temp= command.trim().split("\\s+",2);
        keyword = temp[0];



        switch (keyword) {

            case "LOGIN":

                return checkLoginParams(command, parsedResult);

            case "GET":

                return checkGetParams(command, parsedResult);
            case "GET_DICE_LIST":
                return checkGetDiceListParams(command, parsedResult);

            case "SELECT":
                return checkSelectParams(command, parsedResult);

            case "DISCARD":

                return checkDiscardParams(command, parsedResult);

            case "CHOOSE":
                return checkChooseParams(command, parsedResult);

            case "QUIT":
                return checkQuitParams(command, parsedResult);

            case "ACK":
                return checkAckParams(command, parsedResult);
            default:
                return false;

        }
    }

    /**
     * This method checks if the ACK parameters are valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    public static boolean checkAckParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("ACK")){ return false; }
        if(command.length==2) {
            switch (command[1]) {
                case "game":
                case "send":
                case "list":
                case "status":
                    parsedResult.addAll(Arrays.asList(command));
                    return true;

                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * This method checks if the CHOOSE parameters are valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    public static boolean checkChooseParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("CHOOSE")){ return false; }
        switch (command[1]){
            case "die_placement":
                if((command.length == 3) && command[2].matches(CELL_INDEX)){
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                }

                return false;
            case "die":
                return checkChooseDie(parsedResult, command);


            case "schema":
                if((command.length == 3) && command[2].matches(PLAYER_ID)){
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                }
                return false;

            case "tool":
                if((command.length == 3) && command[2].matches(TOOL_INDEX)){
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                }
                return false;

            case "face":
                if((command.length == 3) && command[2].matches(DIE_FACE)){
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                }
                return false;

            default:
                return false;
        }
    }

    /**
     * This method checks if the GET parameters are valid
     * @param command the string array containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    private static Boolean checkChooseDie(List<String> parsedResult, String[] command) {
        if((command.length == 3) && command[2].matches(TWO_DIGITS_MAX)){
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        if((command.length == 4) && command[2].matches(DRAFT_POOL_DIE)){
            switch (command[3]){
                case "increase":
                case "decrease":
                case "reroll":
                case "flip":
                case "put_in_bag":
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * This method checks if the DISCARD parameters are valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    public static boolean checkDiscardParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("DISCARD")){ return false; }
        if(command.length==1){
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }

    /**
     * This method checks if the QUIT parameters are valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    public static boolean checkQuitParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("QUIT")){ return false; }
        if(command.length==1){
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }

    /**
     * This method checks if the SELECT parameters are valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    public static boolean checkSelectParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("SELECT")){ return false; }
        if(command.length>=2) {
            switch (command[1]){
                case "die":
                    if(command.length==3 && command[2].matches(TWO_DIGITS_MAX)){
                        parsedResult.addAll(Arrays.asList(command));
                        return true;
                    }
                    return false;

                case "modified_die":
                    if(command.length==2){
                        parsedResult.addAll(Arrays.asList(command));
                        return true;
                    }
                    return false;

                case "tool":
                    if(command.length==3 && command[2].matches(TOOL_INDEX)){
                        parsedResult.addAll(Arrays.asList(command));
                        return true;
                    }
                    return false;

                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * This method checks if the GET_DICE_LIST parameters are valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    public static boolean checkGetDiceListParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("GET_DICE_LIST")){ return false; }
        if(command.length==2) {
            switch (command[1]) {
                case "schema":
                case "draftpool":
                case "roundtrack":
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * This method checks if the GET parameters are valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    public static boolean checkGetParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("GET")){ return false; }
        if(command.length>=2) {

            switch (command[1]) {

                case "schema":
                    if ((command.length == 3)
                            && (command[2].equals("draft")
                            || command[2].matches(PLAYER_ID))) {

                        parsedResult.addAll(Arrays.asList(command));
                        return true;
                    }
                    return false;

                case "favor_tokens":
                    if ((command.length == 3)
                            && (command[2].matches(PLAYER_ID))) {

                        parsedResult.addAll(Arrays.asList(command));
                        return true;
                    }
                    return false;

                case "priv":
                case "pub":
                case "tool":
                case "draftpool":
                case "roundtrack":
                case "players":
                    if (command.length == 2) {
                        parsedResult.addAll(Arrays.asList(command));
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        }

        return false;
    }


    /**
     * This method checks if the login parameters are valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters are valid
     * @return true iff the parameters are valid
     */
    public static boolean checkLoginParams(String  rawCommand, List<String> parsedResult){
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("LOGIN")){ return false; }
        if(command.length!=3||!isValidUsername(command[1])){
            return false;
        }

        parsedResult.addAll(Arrays.asList(command));
        return true;
    }

    /**
     * Checks if the the passed username is valid (which means it doesn't contain any non-ALPHANUMERIC character
     * and doesn't begin with a number)
     * @param username the username to check
     * @return true iff the username has a valid format
     */
    public static boolean isValidUsername(String username){
        return  username.matches(ALPHANUMERIC)
                && !username.matches(BEGINS_WITH_DIGIT);
    }


}





















