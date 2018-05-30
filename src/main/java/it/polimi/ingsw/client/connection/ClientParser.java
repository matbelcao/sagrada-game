package it.polimi.ingsw.client.connection;

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

            case "LOGIN":
                return checkLogin(parsedResult);
            case "LOBBY":
                return checkLobby(parsedResult);
            case "GAME":
                return checkGame(parsedResult);
            case "SEND":
                return checkSend(parsedResult);
            case "LIST":
                return checkList(parsedResult);
            case "DISCARD":
                return checkDiscard(parsedResult);
            case "CHOICE":
                return checkChoice(parsedResult);
            case "STATUS":
                return checkStatus(parsedResult);
            case "INVALID":
                return true;
            default:
                parsedResult.clear();
                return false;
        }
    }



    public static boolean isLogin(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals("LOGIN");

    }
    public static boolean isLobby(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals("LOBBY");

    }
    public static boolean isGame(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals("GAME");

    }
    public static boolean isSend(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals("SEND");

    }
    public static boolean isList(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals("LIST");

    }
    public static boolean isDiscard(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals("DISCARD");

    }
    public static boolean isChoice(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals("CHOICE");

    }
    public static boolean isStatus(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals("STATUS");

    }

    public static boolean isInvalid(String message){
        if (message == null) throw new IllegalArgumentException();
        return message.trim().split("\\s+",2)[0].equals("INVALID");
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
        if(parsedResult.size()<3){return false;}
        switch(parsedResult.get(1)){
            case "start":
                return parsedResult.size() == 4;
            case "end":
                for(int i=2;i<parsedResult.size();i++)
                    if (parsedResult.get(i).split(",").length != 3) {
                        return false;
                    }
                return true;
            case "round_start":
                return parsedResult.size() == 3;
            case "round_end":
                return parsedResult.size() == 3;
            case "turn_start":
                return parsedResult.size() == 4;
            case "turn_end":
                return parsedResult.size() == 4;
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
        if(parsedResult.get(1).equals("schema") && parsedResult.size()>3) {
            return checkSendSchema(parsedResult);
        }
        if(parsedResult.get(1).equals("priv")||parsedResult.get(1).equals("pub")||parsedResult.get(1).equals("tool")){
            return parsedResult.size() == 6;
        }
        if(parsedResult.get(1).equals("draftpool")||parsedResult.get(1).equals("roundtrack")||parsedResult.get(1).equals("roundtrack_update")){
            return checkCommaParametersLength(3,parsedResult);
        }
        if(parsedResult.get(1).equals("players")){
            return checkCommaParametersLength(2,parsedResult);
        }
        return false;
    }

    /**
     * Tis method checks if the "SEND schema" parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkSendSchema(List<String> parsedResult) {
        for (int i = 3; i < parsedResult.size(); i++) {
            String[] args = parsedResult.get(i).split(",");
            if (args[0].equals("D") && args.length != 4) {
                return false;
            }
            if (args[0].equals("C") && args.length != 3) {
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
     * This method checks if the LIST parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkList(List<String> parsedResult){
        if(parsedResult.size()<2){return false;}
        if(parsedResult.get(1).equals("schema")||parsedResult.get(1).equals("roundtrack")||parsedResult.get(1).equals("draftpool")) {
            for (int i = 2; i < parsedResult.size(); i++) {
                if (parsedResult.get(i).split(",").length < 3 || parsedResult.get(i).split(",").length > 5) {
                    return false;
                }
            }
            return true;
        }
        if(parsedResult.get(1).equals("placements")){
            for (int i = 2; i < parsedResult.size(); i++) {
                if (parsedResult.get(i).split(",").length != 2) {
                    return false;
                }
            }
            return true;
        }
        return parsedResult.get(1).equals("tool_details") && parsedResult.size() == 6;
    }

    /**
     * This method checks if the DISCARD parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkDiscard(List<String> parsedResult){
        return parsedResult.size() == 2;
    }

    /**
     * This method checks if the CHOICE parameters have a correct number of arguments (nothing has been lost during the communication) and format
     * @param parsedResult the parsed parameters of the command
     * @return true iff the parameters are valid
     */
    private static boolean checkChoice(List<String> parsedResult){
        if(parsedResult.size()<2){return false;}
        if(parsedResult.size()==2){return true;}
        if(parsedResult.get(2).equals("modified_die")){
            return parsedResult.get(3).split(",").length <= 2;
        }
        return parsedResult.get(2).equals("rerolled_dice") && parsedResult.size() == 3 ;
    }

    private static boolean checkStatus(List<String> parsedResult) {
        return parsedResult.size() == 2||parsedResult.size() == 3;
    }
}
