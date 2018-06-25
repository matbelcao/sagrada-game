package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.connection.SocketString;

import java.util.Arrays;
import java.util.List;

/**
 * This class implements various static methods that are used to validate client socket messages, it also offers a simple method to parse commands splitting them on whitespaces
 */
public class Validator {
    private static final String ALPHANUMERIC="[0-9a-zA-Z]+";
    private static final String BEGINS_WITH_DIGIT="^([0-9]+)[^0-9]*";
    private static final String PLAYER_ID="[0-3]";
    private static final String TWO_DIGITS_MAX = "[0-9]|([1-9][0-9])";
    private static final String TOOL_INDEX = "[0-2]";
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

            case SocketString.LOGIN:
                return checkLoginParams(command, parsedResult);

            case SocketString.GAME:
                return checkGameParams(command,parsedResult);

            case SocketString.GET:
                return checkGetParams(command, parsedResult);

            case SocketString.GET_DICE_LIST:
                return checkGetDiceList(command,parsedResult);

            case SocketString.SELECT:
                return checkSelectParams(command, parsedResult);

            case SocketString.CHOOSE:
                return checkChooseParams(command, parsedResult);

            case SocketString.GET_PLACEMENTS_LIST:
                return checkPlacementsDiceList(command,parsedResult);

            case SocketString.TOOL:
                return checkToolParams(command,parsedResult);

            case SocketString.DISCARD:
                return checkDiscard(command,parsedResult);

            case SocketString.BACK:
                return checkBack(command,parsedResult);

            case SocketString.QUIT:
                return checkQuit(command,parsedResult);

            case SocketString.PONG:
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
        if(!command[0].equals(SocketString.LOGIN)){ return false; }
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

    private static boolean checkGameParams(String rawCommand, List<String> parsedResult){
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(!command[0].equals(SocketString.GAME)){ return false; }
        if(command.length==2) {
            switch (command[1]) {
                case SocketString.END_TURN:
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                case SocketString.NEW_MATCH:
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
        if(!command[0].equals(SocketString.GET)){ return false; }
        if(command.length>=2) {
            switch (command[1]) {

                case SocketString.SCHEMA:
                    if ((command.length == 3)
                            && (command[2].equals(SocketString.DRAFTED)
                            || command[2].matches(PLAYER_ID))) {

                        parsedResult.addAll(Arrays.asList(command));
                        return true;
                    }
                    return false;

                case SocketString.TOKENS:
                    if ((command.length == 3)
                            && (command[2].matches(PLAYER_ID))) {

                        parsedResult.addAll(Arrays.asList(command));
                        return true;
                    }
                    return false;

                case SocketString.PRIVATE:
                case SocketString.PUBLIC:
                case SocketString.TOOLCARD:
                case SocketString.DRAFTPOOL:
                case SocketString.ROUNDTRACK:
                case SocketString.GAME_STATUS:
                case SocketString.PLAYERS:
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
    private static boolean checkGetDiceList(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(command.length == 1 && command[0].equals(SocketString.GET_DICE_LIST)){
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
    private static boolean checkPlacementsDiceList(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(command.length == 1 && command[0].equals(SocketString.GET_PLACEMENTS_LIST)){
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
    private static boolean checkSelectParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(command.length!=2 || !command[0].equals(SocketString.SELECT) ){ return false; }

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
    private static boolean checkChooseParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(command.length!=2 || !command[0].equals(SocketString.CHOOSE)){ return false; }
        if(command[1].matches(TWO_DIGITS_MAX)) {
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }



    private static boolean checkToolParams(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        parsedResult.clear();
        if(command.length<2 || !command[0].equals(SocketString.TOOL)){ return false; }
        switch (command[1]){
            case SocketString.ENABLE:
                if((command.length == 3) && command[2].matches(TOOL_INDEX)){
                    parsedResult.addAll(Arrays.asList(command));
                    return true;
                }
                return false;

            case SocketString.CONTINUE:
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
    private static boolean checkDiscard(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        if(!command[0].equals(SocketString.DISCARD)){ return false; }

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
    private static boolean checkBack(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        if(!command[0].equals(SocketString.BACK)){ return false; }

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
    private static boolean checkQuit(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        if(!command[0].equals(SocketString.QUIT)){ return false; }

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
    private static boolean checkPong(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ return false; }
        String [] command= rawCommand.trim().split("\\s+");
        if(!command[0].equals(SocketString.PONG)){ return false; }

        parsedResult.clear();
        if(command.length == 1) {
            parsedResult.addAll(Arrays.asList(command));
            return true;
        }
        return false;
    }

}





















