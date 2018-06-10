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
    private static final int MAX_USERNAME_LENGTH = 16;

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

            case "GAME":
                return checkGameParams(command,parsedResult);

            case "GET":
                return checkGetParams(command, parsedResult);

            case "GET_DICE_LIST":
                return checkGetDiceList(command,parsedResult);

            case "SELECT":
                return checkSelectParams(command, parsedResult);

            case "CHOOSE":
                return checkChooseParams(command, parsedResult);

            case "GET_PLACEMENTS_LIST":
                return checkPlacementsDiceList(command,parsedResult);

            case "TOOL":
                return checkToolParams(command,parsedResult);

            case "DISCARD":
                return checkDiscard(command,parsedResult);

            case "EXIT":
                return checkExit(command,parsedResult);

            case "QUIT":
                return checkQuit(command,parsedResult);

            case "PONG":
                return checkPong(command,parsedResult);

            default:
                return false;

        }
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
                && !username.matches(BEGINS_WITH_DIGIT)
                && username.length()<=MAX_USERNAME_LENGTH;
    }

    public static boolean checkGameParams(String rawCommand, List<String> parsedResult){
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals("GAME")){ return false; }
        if(command.length==2) {
            switch (command[1]) {
                case "end_turn":
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
     * This method checks if the SELECT_DIE command is valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @return true iff the syntax is valid
     */
    public static boolean checkGetDiceList(String rawCommand,List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(command.length == 1 && command[0].equals("GET_DICE_LIST")){
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }

    /**
     * This method checks if the GET_DICE_LIST command is valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @return true iff the syntax is valid
     */
    public static boolean checkPlacementsDiceList(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(command.length == 1 && command[0].equals("GET_PLACEMENTS_LIST")){
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
        if(command.length!=2 || !command[0].equals("SELECT") ){ return false; }

        if(command[1].matches(TWO_DIGITS_MAX)){
            parsedResult.addAll(Arrays.asList(command));
            return true;
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
        if(command.length!=2 || !command[0].equals("CHOOSE")){ return false; }
        if(command[1].matches(TWO_DIGITS_MAX)) {
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }



    public static boolean checkToolParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(command.length<2 || !command[0].equals("TOOL")){ return false; }
        switch (command[1]){
            case "enable":
                if((command.length == 3) && command[2].matches(TOOL_INDEX)){
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                }
                return false;

            case "can_continue":
                if((command.length == 2)){
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * This method checks if the DISCARD syntax is valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @return true iff the parameters are valid
     */
    public static boolean checkDiscard(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        if(!command[0].equals("DISCARD")){ return false; }

        parsedResult.clear();
        if(command.length == 1) {
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }

    /**
     * This method checks if the EXIT syntax is valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @return true iff the parameters are valid
     */
    public static boolean checkExit(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        if(!command[0].equals("EXIT")){ return false; }

        parsedResult.clear();
        if(command.length == 1) {
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }

    /**
     * This method checks if the QUIT syntax is valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @return true iff the parameters are valid
     */
    public static boolean checkQuit(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        if(!command[0].equals("QUIT")){ return false; }

        parsedResult.clear();
        if(command.length == 1) {
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }

    /**
     * This method checks if the PONG message is valid
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @return true iff the parameters are valid
     */
    public static boolean checkPong(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        if(!command[0].equals("PONG")){ return false; }

        parsedResult.clear();
        if(command.length == 1) {
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }

}





















