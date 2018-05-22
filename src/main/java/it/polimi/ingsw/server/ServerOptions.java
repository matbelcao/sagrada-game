package it.polimi.ingsw.server;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ConnectionMode;
import it.polimi.ingsw.client.UIMode;
import it.polimi.ingsw.server.connection.MasterServer;
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

public class ServerOptions {
    private static final String LONG_OPTION="\\-\\-(([a-z]+\\-[a-z]+)|[a-z]+)";
    private static final String SHORT_OPTION="\\-[a-z]+";
    private static final String IP_ADDRESS="^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    private static final String SECONDS="([1-9][0-9])|[1-9]|([1-9][0-9][0-9])";


    public static void printHelpMessage(){
        String message="ERROR: couldn't load configuration files\n";

        File xmlFile= new File(MasterServer.XML_SOURCE+"helpmessage.xml");
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
    public static List<String> getOptions(String[] args){
        ArrayList<String> options= new ArrayList<>();
        int index;
        for(index=0;index< args.length;index++){
            String option=args[index];

            if(option.matches(LONG_OPTION)){
                checkLongOptions(args, options, index, option);
            }
            if(option.matches(SHORT_OPTION)){

                checkShortOptions(args, options, index, option);

            }
        }

        checkValidCombinations(options);

        return options;
    }

    /**
     * Checks if the command-line arguments that are double-dashed options are valid and not repetitions
     * @param args the command line args
     * @param options the list of checked options that is being created
     * @param index the index in the args array
     * @param option the option to be checked
     */
    private static void checkLongOptions(String[] args, ArrayList<String> options, int index, String option) {
        switch(option){

            case "--server-address":
                if(options.contains("a")){ throw new IllegalArgumentException(); }
                if(args[index+1].matches(IP_ADDRESS)){
                    options.add("a");
                    options.add(args[index+1]);
                }else { throw new IllegalArgumentException(); }
                break;
            case "--turn-time":
                if(options.contains("t")){ throw new IllegalArgumentException(); }
                if(args[index+1].matches(SECONDS)){
                    options.add("t");
                    options.add(args[index+1]);
                }else { throw new IllegalArgumentException(); }
                break;
            case "--lobby-time":
                if(options.contains("l")){ throw new IllegalArgumentException(); }
                if(args[index+1].matches(SECONDS)){
                    options.add("l");
                    options.add(args[index+1]);
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
    private static void checkShortOptions(String[] args, ArrayList<String> options, int index, String option) {
        int i;
        String shortOption;
        i=1;
        while(i < option.length()){
            shortOption=option.substring(i,i+1);
            if(shortOption.matches("[hastl]")){

                if(options.contains(shortOption)){ throw new IllegalArgumentException(); }
                options.add(shortOption);
            }else if(shortOption.equals("a") && i==option.length()-1){
                if(args.length>index+1 && args[index+1].matches(IP_ADDRESS)){
                    options.add("a");
                    options.add(args[index+1]);
                }else {
                    throw new IllegalArgumentException();
                }
            } else if (shortOption.equals("t") && i==option.length()-1) {
                if(args.length>index+1 && args[index+1].matches(SECONDS)){
                    options.add("t");
                    options.add(args[index+1]);
                }else {
                    throw new IllegalArgumentException();
                }

            }else if (shortOption.equals("g") && i==option.length()-1) {
                if (args.length > index + 1 && args[index + 1].matches(SECONDS)) {
                    options.add("g");
                    options.add(args[index + 1]);
                } else {
                    throw new IllegalArgumentException();
                }
            }else {
                throw new IllegalArgumentException();
            }
            i++;
        }
    }

    private static void checkValidCombinations(ArrayList<String> options) {
        if( (options.contains("r")&& options.contains("s"))||(options.contains("g") && options.contains("c")) || (options.contains("h")&& options.size()>1) ){
            throw new IllegalArgumentException();
        }
    }

    public static void setServerPreferences(ArrayList<String> options, MasterServer server) {

    }
}
