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
                if(!checkOptionWithParam(options, args, index, IP_ADDRESS, "a")) {
                throw new IllegalArgumentException();
            }
                break;

            case "--turn-time":
                if(!checkOptionWithParam(options, args, index, SECONDS, "t")) {
                throw new IllegalArgumentException();
            }
                break;

            case "--lobby-time":
                if(!checkOptionWithParam(options, args, index, SECONDS, "l")) {
                    throw new IllegalArgumentException();
                }
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
        i = 1;
        while (i < option.length()) {
            shortOption = option.substring(i, i + 1);

            //invalid option or already set
            if (!shortOption.matches("[haAtl]") || options.contains(shortOption)) {
                throw new IllegalArgumentException();
            }

            //options without parameters
            if (shortOption.matches("[Ah]")) {
                options.add(shortOption);
            } else if (!isLastShortOption(option, i)) {
                //options with parameters must be at the end of a set of short options following the same '-'
                throw new IllegalArgumentException();
            }
            switch (shortOption) {
                case "l":
                case "t":
                    if (!checkOptionWithParam(options, args, index, SECONDS, shortOption)) {
                        throw new IllegalArgumentException();
                    }
                    break;
                case "a":
                    if (!checkOptionWithParam(options, args, index, IP_ADDRESS, shortOption)) {
                        throw new IllegalArgumentException();
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
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

    public static void setServerPreferences(List<String> options, MasterServer server) {
        if(options.contains("A")){ server.setAdditionalSchemas(true);}
        if(options.contains("a")){ server.setIpAddress(options.get(options.indexOf("a")+1));}
        if(options.contains("t")){ server.setTurnTime(Integer.parseInt(options.get(options.indexOf("t")+1)));}
        if(options.contains("l")){ server.setLobbyTime((Integer.parseInt(options.get(options.indexOf("l")+1))));}


    }
}
