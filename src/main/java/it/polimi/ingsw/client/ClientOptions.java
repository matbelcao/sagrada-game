package it.polimi.ingsw.client;

import it.polimi.ingsw.client.controller.Client;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMode;
import it.polimi.ingsw.common.enums.ConnectionMode;
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

public class ClientOptions {
    public static final String SHORT_HELP ="h";
    public static final String SHORT_GUI ="g";
    public static final String SHORT_CLI ="c";
    public static final String SHORT_RMI ="r";
    public static final String SHORT_SOCKET ="s";
    public static final String SHORT_ADDRESS ="a";
    public static final String SHORT_ITA ="i";
    public static final String SHORT_ENG ="e";
    public static final String ALL_SHORTS = "[hgcarsie]";
    public static final String SHORTS_NO_PARAMS = "[hgcrsie]";
    public static final String LONG_GUI = "--gui";
    public static final String LONG_CLI = "--cli";
    public static final String LONG_RMI = "--rmi";
    public static final String LONG_SOCKET = "--socket";
    public static final String LONG_ITA = "--italian";
    public static final String LONG_ENG = "--english";
    public static final String LONG_ADDRESS = "--server-address";
    public static final String LONG_HELP = "--help";
    public static final String HELP_MESSAGE = "help-message";

    private ClientOptions(){}

    private static final String LONG_OPTION="(\\-\\-(([a-z]+\\-[a-z]+)|[a-z]+))";
    private static final String SHORT_OPTION="(\\-"+ALL_SHORTS+"+)";
    private static final String IP_ADDRESS="(^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$)";
    private static final String HELP_FILE= "helpmessage.xml";


    public static void printHelpMessage(){
        String message="ERROR: couldn't load configuration files\n";
        ClassLoader classLoader=ClassLoader.getSystemClassLoader();
        InputStream xmlFile=classLoader.getResourceAsStream(Client.XML_SOURCE+HELP_FILE);
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
    public static boolean getOptions(String[] args, List<String> options){
        if(options==null){ throw new IllegalArgumentException();}
        options.clear();
        int index;
        try {
            for (index = 0; index < args.length; index++) {
                String option = args[index];

                if (!option.matches(LONG_OPTION + "|" + SHORT_OPTION + "|" + IP_ADDRESS)) {
                    throw new IllegalArgumentException();
                }

                if (option.matches(LONG_OPTION)) {
                    checkLongOptions(args, options, index, option);
                }
                if (option.matches(SHORT_OPTION)) {
                    checkShortOptions(args, options, index, option);
                }
                if (option.matches(IP_ADDRESS)) {
                    checkIPOption(options, option);
                }

            }

        checkValidCombinations(options);
        }catch (IllegalArgumentException e) {
            options.clear();
            return false;
        }

        return true;
    }

    /**
     * checks if the ip is in a valid position in the command
     * @param options the list of options that have been parsed until now
     * @param ip the option (ip) to be checked
     */
    private static void checkIPOption(List<String> options, String ip) {
        if(options.get(options.size()-1).equals(SHORT_ADDRESS)){
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
            case LONG_GUI:
            case LONG_CLI:
            case LONG_RMI:
            case LONG_SOCKET:
            case LONG_ITA:
            case LONG_ENG:
                if(options.contains(option.substring(2,3))){ throw new IllegalArgumentException(); }
                options.add(option.substring(2,3));
                break;
            case LONG_ADDRESS:
                if(!checkOptionWithParam(options,args,index,IP_ADDRESS,SHORT_ADDRESS)){
                    throw new IllegalArgumentException();
                }
                break;
            case LONG_HELP:
                if(!options.contains(SHORT_HELP)){ throw new IllegalArgumentException(); }
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
        i=1;
        while(i < option.length()){

            shortOption=option.substring(i,i+1);

            //invalid option or already set
            if(!shortOption.matches(ALL_SHORTS)|| options.contains(shortOption)){
                throw new IllegalArgumentException();
            }

            //options without parameters
            if(shortOption.matches(SHORTS_NO_PARAMS)){
                options.add(shortOption);
            }else {
                if (!isLastShortOption(option, i)) {
                    throw new IllegalArgumentException();
                }
                //options with parameters
                if (!checkOptionWithParam(options,args,index,IP_ADDRESS,shortOption)) { throw new IllegalArgumentException();}
            }
            i++;
        }
    }

    private static boolean checkOptionWithParam(List<String> options, String[] args, int index, String paramType, String shortOption) {
        if (!options.contains(shortOption)
                && (args.length > (index + 1))
                && args[index + 1].matches(paramType)) {
            options.add(shortOption);
            return true;
        }
        return false;
    }

    private static boolean isLastShortOption(String option, int i) {
        return i==option.length()-1;
    }

    private static void checkValidCombinations(List<String> options) {
        if( (options.contains(SHORT_RMI)&& options.contains(SHORT_SOCKET))
                || (options.contains(SHORT_GUI) && options.contains(SHORT_CLI))
                || (options.contains(SHORT_ITA) && options.contains(SHORT_ENG))
                || (options.contains(SHORT_HELP)&& options.size()>1) ){
            throw new IllegalArgumentException();
        }
    }

    public static void setClientPreferences(List<String> options, Client client) {
        if(options.contains(SHORT_RMI)){ client.setConnMode(ConnectionMode.RMI);}
        if(options.contains(SHORT_SOCKET)){ client.setConnMode(ConnectionMode.SOCKET);}
        if(options.contains(SHORT_GUI)){ client.setUiMode(UIMode.GUI);}
        if(options.contains(SHORT_CLI)){ client.setUiMode(UIMode.CLI);}
        if(options.contains(SHORT_ENG)){ client.setLanguage(UILanguage.ENG);}
        if(options.contains(SHORT_ITA)){ client.setLanguage(UILanguage.ITA);}
        if(options.contains(SHORT_ADDRESS)){ client.setServerIP(options.get(options.indexOf(SHORT_ADDRESS)+1));}
    }
}
