package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.connection.SocketString;

import java.util.Arrays;
import java.util.List;

/**
 * This class implements the static methods that is used to validate(and parse) the server-side socket messages.
 */
public class ClientParser {

    private ClientParser(){}

    /**
     * This method checks if parameters of a generic server-side command have a valid length
     * @param rawCommand the raw string containing all parameters of the command and the command itself
     * @param parsedResult the parsed parameters this is modified and will contain the parse result only if the parameters have a valid length
     * @return true iff the parameters are valid
     */
    public static boolean parse(String rawCommand, List<String> parsedResult) {
        if( rawCommand==null){ throw new IllegalArgumentException(); }
        String[] command;
        String keyword;

        if(parsedResult==null){throw new IllegalArgumentException();}

        command= rawCommand.trim().split("\\s+");
        keyword = command[0];

        parsedResult.clear();
        parsedResult.addAll(Arrays.asList(command));
        switch (keyword) {

            case SocketString.LOGIN:
                return checkLogin(parsedResult);
            case SocketString.LOBBY:
                return checkLobby(parsedResult);
            case SocketString.GAME:
                return checkGame(parsedResult);
            case SocketString.SEND:
                return checkSend(parsedResult);
            case SocketString.LIST_DICE:
                return checkDiceList(parsedResult);
            case SocketString.LIST_OPTIONS:
                return parsedResult.size()>=2;
            case SocketString.LIST_PLACEMENTS:
                return true;
            case SocketString.CHOICE:
                return checkChoice(parsedResult);
            case SocketString.TOOL:
                return checkTool(parsedResult);
            case SocketString.STATUS:
                return checkStatus(parsedResult);
            case SocketString.PING:
                return parsedResult.size()==1;
            case SocketString.INVALID:
                return true;
            case SocketString.ILLEGAL:
                return true;
            default:
                parsedResult.clear();
                return false;
        }
    }



    public static boolean isLogin(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.LOGIN);

    }

    public static boolean isLobby(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.LOBBY);

    }

    public static boolean isGame(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.GAME);

    }

    public static boolean isSend(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.SEND);

    }

    public static boolean isDiceList(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.LIST_DICE);

    }

    public static boolean isOptionList(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.LIST_OPTIONS);

    }

    public static boolean isPlacementList(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.LIST_PLACEMENTS);

    }

    public static boolean isChoice(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.CHOICE);

    }

    public static boolean isTool(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.TOOL);
    }

    public static boolean isStatus(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.STATUS);

    }

    public static boolean isInvalid(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.INVALID);
    }

    public static boolean isPing(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.PING);
    }

    public static boolean isIllegalAction(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals(SocketString.ILLEGAL);
    }

    /**
     * This method checks if the LOGIN parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkLogin(List<String> parsedResult){
        return parsedResult.size() == 2;
    }

    /**
     * This method checks if the LOBBY parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkLobby(List<String> parsedResult){
        return parsedResult.size() == 2;
    }

    /**
     * This method checks if the GAME parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkGame(List<String> parsedResult){
        if(parsedResult.size()<2){return false;}
        switch(parsedResult.get(1)){
            case SocketString.START:
                return parsedResult.size() == 4;
            case SocketString.END:
                for(int i=2;i<parsedResult.size();i++)
                    if (parsedResult.get(i).split(",").length != 3) {
                        return false;
                    }
                return true;
            case SocketString.ROUND_START:
                return parsedResult.size() == 3;
            case SocketString.ROUND_END:
                return parsedResult.size() == 3;
            case SocketString.TURN_START:
                return parsedResult.size() == 4;
            case SocketString.TURN_END:
                return parsedResult.size() == 4;
            case SocketString.BOARD_CHANGED:
                return true;
            default:
                return false;
        }
    }

    /**
     * This method checks if the SEND parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkSend(List<String> parsedResult){
        if(parsedResult.size()<2){return false;}
        if(parsedResult.get(1).equals(SocketString.SCHEMA) && parsedResult.size()>4) {
            return checkSendSchema(parsedResult);
        }
        if(parsedResult.get(1).equals(SocketString.GAME_STATUS)) {
            return true;
        }
        if(parsedResult.get(1).equals(SocketString.TOKENS) && parsedResult.size()==3){return true;}
        if(parsedResult.get(1).equals(SocketString.PRIVATE)||parsedResult.get(1).equals(SocketString.TOOLCARD)){
            return parsedResult.size() == 6 ;
        }
        if(parsedResult.get(1).equals(SocketString.PUBLIC)){
            return parsedResult.size() == 5 ;
        }
        if(parsedResult.get(1).equals(SocketString.DRAFTPOOL)||parsedResult.get(1).equals(SocketString.ROUNDTRACK)){
            return checkCommaParametersLength(3,parsedResult);
        }
        if(parsedResult.get(1).equals(SocketString.PLAYERS)){
            return checkCommaParametersLength(3,parsedResult);
        }
        return false;
    }

    /**
     * Tis method checks if the "SEND schema" parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkSendSchema(List<String> parsedResult) {
        for (int i = 4; i < parsedResult.size(); i++) {
            if(parsedResult.get(i).matches("[0-9]")){
                i++;
            }
            String[] args = parsedResult.get(i).split(",");
            if (args[0].equals(SocketString.DIE) && args.length != 4) {
                return false;
            }
            if (args[0].equals(SocketString.CONSTRAINT) && args.length != 3) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tis method checks if the "SEND xxxx" parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkCommaParametersLength(int validLength,List<String> parsedResult){
        for(int i=2;i<parsedResult.size();i++)
            if (parsedResult.get(i).split(",").length != validLength) {
                return false;
            }
        return true;
    }

    /**
     * This method checks if the DICE LIST parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkDiceList(List<String> parsedResult){
        if(!parsedResult.get(0).equals(SocketString.LIST_DICE)) {return false;}
        for (int i = 2; i < parsedResult.size(); i++) {
            if (parsedResult.get(i).split(",").length != 3) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method checks if the TOOL parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkTool(List<String> parsedResult){
        if(parsedResult.size()!=2){return false;}
        return parsedResult.get(1).equals(SocketString.OK) || parsedResult.get(1).equals(SocketString.KO);
    }

    /**
     * This method checks if the CHOICE parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkChoice(List<String> parsedResult){
        if(parsedResult.size()!=2){return false;}
        return parsedResult.get(1).equals(SocketString.OK) || parsedResult.get(1).equals(SocketString.KO);
    }

    private static boolean checkStatus(List<String> parsedResult) {
        return parsedResult.size()>=2;
    }
}
