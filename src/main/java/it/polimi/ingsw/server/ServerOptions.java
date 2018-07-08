package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.MasterServer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * this implements the parser for the args passed to the master server
 */
public class ServerOptions {
    private static final String HELP_MESSAGE ="help-message" ;
    private static final String HELP_FILE = "helpmessage.xml";
    private static final String SHORT_HELP = "h";
    private static final String SHORT_IP_ADDRESS = "a";
    private static final String SHORT_ADDITIONAL_CARDS = "A";
    private static final String SHORT_TURN_TIME = "t";
    private static final String SHORT_LOBBY_TIME = "l";
    private static final String ERR_LOAD_CONFIGURATION_FILES = "ERR: couldn't load configuration files\n";
    private static final String LONG_HELP = "--help";
    private static final String LONG_TURN_TIME = "--turn-time";
    private static final String LONG_LOBBY_TIME = "--lobby-time";
    private static final String LONG_ADDITIONAL_SCHEMAS = "--additional-schemas";
    private static final String LONG_IP_ADDRESS = "--server-address";
    private static final String SHORTS = "[haAtl]";
    private static final String NO_PARAMS_SHORTS = "[Ah]";



    private static final String LONG_OPTION="(\\-\\-(([a-z]+\\-[a-z]+)|[a-z]+))";
    private static final String SHORT_OPTION="\\-"+SHORTS+"+";
    private static final String IP_ADDRESS="(^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$)";
    private static final String SECONDS="(([1-9][0-9])|[0-9]|([1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))";

    private ServerOptions(){}

    /**
     * prints the help message to the screen
     */
    public static void printHelpMessage(){
        String message= ERR_LOAD_CONFIGURATION_FILES;
        ClassLoader classLoader=ClassLoader.getSystemClassLoader();
        InputStream xmlFile=classLoader.getResourceAsStream(MasterServer.XML_SOURCE+HELP_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            message = doc.getElementsByTagName(HELP_MESSAGE).item(0).getTextContent();
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            Logger.getGlobal().log(Level.INFO, e1.getMessage());
        }
        System.out.print(message);
    }


    /**
     * Checks validity of command line arguments and simplifies their retrieval
     * @param args the command line args
     * @return the list of options
     * @throws IllegalArgumentException if invalid options or combinations of options are found
     */
    public static boolean getOptions(String[] args,List<String> options){
        if(options==null){ throw new IllegalArgumentException();}
        options.clear();
        int index;
        try {
            for (index = 0; index < args.length; index++) {
                String option = args[index];

                if (!option.matches(LONG_OPTION + "|" + SHORT_OPTION + "|" + IP_ADDRESS + "|" + SECONDS)) {
                    throw new IllegalArgumentException();
                }

                if (option.matches(LONG_OPTION)) {
                    checkLongOptions(args, options, index, option);
                }else if (option.matches(SHORT_OPTION)) {
                    checkShortOptions(args, options, index, option);
                }else if (option.matches(IP_ADDRESS)) {
                    checkIPOption(options, option);
                }else if (option.matches(SECONDS)) {
                    checkSecondsOption(options, option);
                }
            }
        }catch (IllegalArgumentException e){
            options.clear();
            return false;
        }
        return true;
    }

    /**
     * checks if the seconds param is correctly placed in the command
     * @param options the list of options that have been parsed until now
     * @param time the parameter to be checked
     */
    private static void checkSecondsOption(List<String> options, String time) {
        if(options.get(options.size()-1).equals(SHORT_TURN_TIME) || options.get(options.size()-1).equals(SHORT_LOBBY_TIME)){
            options.add(time);
        }else{ throw new IllegalArgumentException();}
    }

    /**
     * checks if the ip is in a valid position in the command
     * @param options the list of options that have been parsed until now
     * @param ip the option (ip) to be checked
     */
    private static void checkIPOption(List<String> options, String ip) {
        if(options.get(options.size()-1).equals(SHORT_IP_ADDRESS)){
            options.add(ip);
        }else{ throw new IllegalArgumentException();}

    }
    /**
     * Checks if the command-line arguments that are double-dashed options are valid and not repetitions
     * @param args the command line args
     * @param options the list of checked options that is being created
     * @param index the index in the args array
     * @param option the option to be checked
     */
    private static void checkLongOptions(String[] args, List<String> options, int index, String option) {
        switch(option){

            case LONG_IP_ADDRESS:
                if(!checkOptionWithParam(options, args, index, IP_ADDRESS, SHORT_IP_ADDRESS)) {
                throw new IllegalArgumentException();
            }
                break;

            case LONG_TURN_TIME:
                if(!checkOptionWithParam(options, args, index, SECONDS, SHORT_TURN_TIME)) {
                throw new IllegalArgumentException();
            }
                break;

            case LONG_LOBBY_TIME:
                if(!checkOptionWithParam(options, args, index, SECONDS, SHORT_LOBBY_TIME)) {
                    throw new IllegalArgumentException();
                }
                break;

            case LONG_ADDITIONAL_SCHEMAS:
                if(options.contains(SHORT_ADDITIONAL_CARDS)){ throw new IllegalArgumentException(); }
                    options.add(SHORT_ADDITIONAL_CARDS);
                break;

            case LONG_HELP:
                if(options.contains(SHORT_HELP)){ throw new IllegalArgumentException(); }
                options.add(SHORT_HELP);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Checks if the command-line arguments that are single-dashed options are valid and not repetitions
     * @param args the command line args
     * @param options the list of checked options that is being created
     * @param index the index in the args array
     * @param option the option/options to be checked
     */
    private static void checkShortOptions(String[] args, List<String> options, int index, String option) {
        int i;
        String shortOption;
        i = 1;
        while (i < option.length()) {
            shortOption = option.substring(i, i + 1);

            //invalid option or already set
            if (!shortOption.matches(SHORTS) || options.contains(shortOption)) {
                throw new IllegalArgumentException();
            }

            //options without parameters
            if (shortOption.matches(NO_PARAMS_SHORTS)) {
                options.add(shortOption);
            } else {
                if (!isLastShortOption(option, i)) {
                    //options with parameters must be at the end of a set of short options following the same '-'
                    throw new IllegalArgumentException();
                }
                switch (shortOption) {
                    case SHORT_LOBBY_TIME:
                    case SHORT_TURN_TIME:
                        if (!checkOptionWithParam(options, args, index, SECONDS, shortOption)) {
                            throw new IllegalArgumentException();
                        }
                        break;
                    case SHORT_IP_ADDRESS:
                        if (!checkOptionWithParam(options, args, index, IP_ADDRESS, shortOption)) {
                            throw new IllegalArgumentException();
                        }
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            i++;
        }
    }

    /**
     * checks that the param for the option is valid
     * @param options the list of options added until now
     * @param args the args from app launch
     * @param index the index
     * @param paramType the type of the param to be checked on
     * @param shortOption the corresponding short option
     * @return iff the param for the option is valid
     */
    private static boolean checkOptionWithParam(List<String> options, String[] args, int index, String paramType, String shortOption) {
        if (!options.contains(shortOption)
                && (args.length > (index + 1))
                && args[index + 1].matches(paramType)) {
            options.add(shortOption);
            return true;
        }
        return false;
    }

    /**
     * @param option the shortoptions string
     * @param i the index in said string
     * @return true iff i points to the last char of the string
     */
    private static boolean isLastShortOption(String option, int i) {
        return i==option.length()-1;
    }

    /**
     * this sets the parsed preferences applying them to the server
     * @param options the list of parsed options
     * @param server the instance of the server to which apply the prefs
     */
    public static void setServerPreferences(List<String> options, MasterServer server) {
        if(options.contains(SHORT_ADDITIONAL_CARDS)){ server.setAdditionalSchemas(true);}
        if(options.contains(SHORT_IP_ADDRESS)){ server.setIpAddress(options.get(options.indexOf(SHORT_IP_ADDRESS)+1));}
        if(options.contains(SHORT_TURN_TIME)){ server.setTurnTime(Integer.parseInt(options.get(options.indexOf(SHORT_TURN_TIME)+1)));}
        if(options.contains(SHORT_LOBBY_TIME)){ server.setLobbyTime((Integer.parseInt(options.get(options.indexOf(SHORT_LOBBY_TIME)+1))));}


    }
}
