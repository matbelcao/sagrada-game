package it.polimi.ingsw.server;

import it.polimi.ingsw.server.connection.MasterServer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.System.out;

public class ServerOptions {
    private ServerOptions(){}

    private static final String LONG_OPTION="(\\-\\-(([a-z]+\\-[a-z]+)|[a-z]+))";
    private static final String SHORT_OPTION="\\-[haAtl]+";
    private static final String IP_ADDRESS="(^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$)";
    private static final String SECONDS="(([1-9][0-9])|[1-9]|([1-9][0-9][0-9]))";


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
                }
                if (option.matches(SHORT_OPTION)) {
                    checkShortOptions(args, options, index, option);
                }
                if (option.matches(IP_ADDRESS)) {
                    checkIPOption(options, option);
                }
                if (option.matches(SECONDS)) {
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
        if(options.get(options.size()-1).equals("t") || options.get(options.size()-1).equals("l")){
            options.add(time);
        }else{ throw new IllegalArgumentException();}
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

            case "--server-address":
                if(options.contains("a")){ throw new IllegalArgumentException(); }
                if(args[index+1].matches(IP_ADDRESS)){
                    options.add("a");
                }else { throw new IllegalArgumentException(); }
                break;

            case "--turn-time":
                if(options.contains("t")){ throw new IllegalArgumentException(); }
                if(args[index+1].matches(SECONDS)){
                    options.add("t");
                }else { throw new IllegalArgumentException(); }
                break;

            case "--lobby-time":
                if(options.contains("l")){ throw new IllegalArgumentException(); }
                if(args[index+1].matches(SECONDS)){
                    options.add("l");
                }else { throw new IllegalArgumentException(); }
                break;

            case "--additional-schemas":
                if(options.contains("A")){ throw new IllegalArgumentException(); }
                    options.add("A");
                break;

            case "--help":
                if(options.contains("h")){ throw new IllegalArgumentException(); }
                options.add("h");
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
            if(!shortOption.matches("[haAtl]")){ throw new IllegalArgumentException(); }

            //option already set
            if(options.contains(shortOption)){ throw new IllegalArgumentException(); }

            //options without parameters
            if(shortOption.matches("[Ah]")){
                options.add(shortOption);
            }else {
                //options with parameters
                if (isLastShortOption(option, i)) {
                    if (shortOption.equals("a")) {
                        if ((args.length > (index + 1)) && args[index + 1].matches(IP_ADDRESS)) {
                            options.add("a");
                            return;
                        }
                        throw new IllegalArgumentException();
                    }

                    if (shortOption.equals("t")) {
                        if ((args.length > (index + 1)) && args[index + 1].matches(SECONDS)) {
                            options.add("t");
                            return;
                        }
                        throw new IllegalArgumentException();
                    }

                    if (shortOption.equals("l")) {
                        if ((args.length > (index + 1)) && args[index + 1].matches(SECONDS)) {
                            options.add("l");
                            return;
                        }
                        throw new IllegalArgumentException();
                    }

                }
            }
            i++;
        }
    }

    private static boolean isLastShortOption(String option, int i) {
        return i==option.length()-1;
    }

    public static void setServerPreferences(List<String> options, MasterServer server) {
        if(options.contains("A")){ server.setAdditionalSchemas(true);}
        if(options.contains("a")){ server.setIpAddress(options.get(options.indexOf("a")+1));}
        if(options.contains("t")){ server.setTimeGame(Integer.parseInt(options.get(options.indexOf("t")+1)));}
        if(options.contains("l")){ server.setTimeLobby((Integer.parseInt(options.get(options.indexOf("l")+1))));}


    }
}
