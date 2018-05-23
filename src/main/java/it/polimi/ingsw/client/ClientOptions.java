package it.polimi.ingsw.client;

import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.UIMode;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ClientOptions {
    private static final String LONG_OPTION="(\\-\\-(([a-z]+\\-[a-z]+)|[a-z]+))";
    private static final String SHORT_OPTION="(\\-[hgcrsa]+)";
    private static final String IP_ADDRESS="(^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$)";



    public static void printHelpMessage(){
        String message="ERROR: couldn't load configuration files\n";

        File xmlFile= new File(Client.XML_SOURCE+"helpmessage.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            message = doc.getElementsByTagName("help-message").item(0).getTextContent();
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
        out.print(message);
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
        }catch (IllegalArgumentException e) {
            options.clear();
            return false;
        }

        checkValidCombinations(options);

        return true;
    }

    /**
     * checks if the ip is in a valid position in the command
     * @param options the list of options that have been parsed until now
     * @param ip the option (ip) to be checked
     */
    private static void checkIPOption(List<String> options, String ip) {
        if(options.get(options.size()-1).equals("a")){
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
            case "--gui":
            case "--cli":
            case "--rmi":
            case "--socket":
                if(options.contains(option.substring(2,3))){ throw new IllegalArgumentException(); }
                options.add(option.substring(2,3));
                break;
            case "--server-address":
                if(options.contains("a")){ throw new IllegalArgumentException(); }
                if(args[index+1].matches(IP_ADDRESS)){
                    options.add("a");
                }else { throw new IllegalArgumentException(); }
                break;
            case "--help":
                printHelpMessage();
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

            //invalid option
            if(!shortOption.matches("[hgcars]")){ throw new IllegalArgumentException(); }

            //option already added
            if(options.contains(shortOption)){ throw new IllegalArgumentException(); }

            //options without parameters
            if(shortOption.matches("[hgcrs]")){
                options.add(shortOption);
            }else {
                if (isLastShortOption(option, i)) {
                    //options with parameters
                    if (shortOption.equals("a")) {
                        if ((args.length > (index + 1)) && args[index + 1].matches(IP_ADDRESS)) {
                            options.add("a");
                            return;
                        }
                        throw new IllegalArgumentException();
                    }
                } else {
                    throw new IllegalArgumentException();
                }
            }
            i++;
        }
    }

    private static boolean isLastShortOption(String option, int i) {
        return i==option.length()-1;
    }
    private static void checkValidCombinations(ArrayList<String> options) {
        if( (options.contains("r")&& options.contains("s"))||(options.contains("g") && options.contains("c")) || (options.contains("h")&& options.size()>1) ){
            throw new IllegalArgumentException();
        }
    }

    public static void setClientPreferences(List<String> options, Client client) {
        if(options.contains("r")){ client.setConnMode(ConnectionMode.RMI);}
        if(options.contains("s")){ client.setConnMode(ConnectionMode.SOCKET);}
        if(options.contains("g")){ client.setUiMode(UIMode.GUI);}
        if(options.contains("c")){ client.setUiMode(UIMode.CLI);}
        if(options.contains("a")){ client.setServerIP(options.get(options.indexOf("a")+1));}
    }
}
