package it.polimi.ingsw.client;

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

public class ClientOptions {

    private ClientOptions(){}

    private static final String LONG_OPTION="(\\-\\-(([a-z]+\\-[a-z]+)|[a-z]+))";
    private static final String SHORT_OPTION="(\\-[hgcrsaie]+)";
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
            case "--italian":
            case "--english":
                if(options.contains(option.substring(2,3))){ throw new IllegalArgumentException(); }
                options.add(option.substring(2,3));
                break;
            case "--server-address":
                if(!checkOptionWithParam(options,args,index,IP_ADDRESS,"a")){
                    throw new IllegalArgumentException();
                }
                break;
            case "--help":
                if(!options.contains("h")){ throw new IllegalArgumentException(); }
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

            //invalid option or already set
            if(!shortOption.matches("[hgcarsie]")|| options.contains(shortOption)){
                throw new IllegalArgumentException();
            }

            //options without parameters
            if(shortOption.matches("[hgcrsie]")){
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
        if( (options.contains("r")&& options.contains("s"))
                || (options.contains("g") && options.contains("c"))
                || (options.contains("i") && options.contains("e"))
                || (options.contains("h")&& options.size()>1) ){
            throw new IllegalArgumentException();
        }
    }

    public static void setClientPreferences(List<String> options, Client client) {
        if(options.contains("r")){ client.setConnMode(ConnectionMode.RMI);}
        if(options.contains("s")){ client.setConnMode(ConnectionMode.SOCKET);}
        if(options.contains("g")){ client.setUiMode(UIMode.GUI);}
        if(options.contains("c")){ client.setUiMode(UIMode.CLI);}
        if(options.contains("e")){ client.setLanguage(UILanguage.eng);}
        if(options.contains("i")){ client.setLanguage(UILanguage.ita);}
        if(options.contains("a")){ client.setServerIP(options.get(options.indexOf("a")+1));}
    }
}
