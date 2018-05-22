package it.polimi.ingsw.client;

import it.polimi.ingsw.client.connection.ClientConn;
import it.polimi.ingsw.client.connection.RMIClient;
import it.polimi.ingsw.client.connection.RMIClientInt;
import it.polimi.ingsw.client.connection.SocketClient;
import it.polimi.ingsw.server.connection.AuthenticationInt;
import it.polimi.ingsw.server.connection.RMIServerInt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class Client {

    private UIMode uiMode;
    private ConnectionMode connMode;
    private String username;
    private String password;
    private ClientConn clientConn;
    private String serverIP;
    private Integer port;
    private CLI cli;
    public static final String XML_SOURCE = "src"+ File.separator+"xml"+File.separator+"client"+ File.separator; //append class name + ".xml" to obtain complete path
    private static final String LONG_OPTION="\\-\\-(([a-z]+\\-[a-z]+)|[a-z]+)";
    private static final String SHORT_OPTION="\\-[a-z]+";
    private static final String IP_ADDRESS="^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";


    public Client() {
        File xmlFile= new File(XML_SOURCE+"ClientConf.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            Element eElement = (Element)doc.getElementsByTagName("conf").item(0);
            this.uiMode=UIMode.valueOf(eElement.getElementsByTagName("UI").item(0).getTextContent());
            this.serverIP=eElement.getElementsByTagName("address").item(0).getTextContent();
            this.connMode=ConnectionMode.valueOf(eElement.getElementsByTagName("connectionMode").item(0).getTextContent());
            if(connMode.equals(ConnectionMode.RMI)){
                this.port=Integer.parseInt(eElement.getElementsByTagName("portRMI").item(0).getTextContent());
            }else{ this.port=Integer.parseInt(eElement.getElementsByTagName("portSocket").item(0).getTextContent()); }
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }

    }

    public Client(UIMode uiMode,ConnectionMode connMode,String serverIP){
        this();
        this.uiMode=uiMode;
        this.connMode=connMode;
        this.serverIP=serverIP;
    }

    public Client(UIMode uiMode,ConnectionMode connMode){
        this();
        this.uiMode=uiMode;
        this.connMode=connMode;
    }

    public Client(String uiMode,String connMode,String serverIP){
        this();
        this.uiMode=UIMode.valueOf(uiMode);
        this.connMode=ConnectionMode.valueOf(connMode);
        this.serverIP=serverIP;
    }

    public Client(String uiMode,String connMode){
        this();
        this.uiMode=UIMode.valueOf(uiMode);
        this.connMode=ConnectionMode.valueOf(connMode);
    }


    public void setUiMode(UIMode uiMode) {
        this.uiMode = uiMode;
    }

    public void setConnMode(ConnectionMode connMode) {
        this.connMode = connMode;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getUsername() { return username; }

    public String getPassword(){ return password; }

    public void setUsername(String username){ this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public boolean setupConnection(){
        if(connMode.equals(ConnectionMode.SOCKET)) {
            clientConn = new SocketClient(serverIP, port);
            cli.updateConnection(); //not correct for RMI, the connection can only  be established after login
        }
        return connMode.equals(ConnectionMode.RMI)? loginRMI(): loginSocket() ;
    }



    public void setConnection(ClientConn clientConn){
        this.clientConn = clientConn;
    }
    public ClientConn getClientConn(){
        return clientConn;
    }

    public CLI getCli(){return cli;}

    public UIMode getUiMode() {
        return uiMode;
    }

     private static void printHelpMessage(){
        String message="ERROR: couldn't load configuration files\n";

        File xmlFile= new File(XML_SOURCE+"helpmessage.xml");
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

    public void login(){
        boolean logged;
        cli=new CLI(this);

        do{
            cli.loginProcedure();
            logged=setupConnection();
            cli.updateLogin(logged);
        }while(!logged);

    }
    private boolean loginRMI(){
        try {
            AuthenticationInt authenticator=(AuthenticationInt) Naming.lookup("rmi://"+serverIP+"/auth");
            if(authenticator.authenticate(username,password)){
               //get the stub of the remote object
               RMIServerInt rmiConnStub = (RMIServerInt) Naming.lookup("rmi://"+serverIP+"/"+username+password);
               //create RMIClient with the reference of the remote obj and assign it to the Client
               RMIClientInt rmiClient  = new RMIClient(rmiConnStub);
               clientConn = (RMIClient)rmiClient;
               //create a remote reference of the obj rmiClient and pass it to the server.
               //a remote reference is passed so there's no need to add rmiClient to a Registry
               RMIClientInt remoteRef = (RMIClientInt) UnicastRemoteObject.exportObject(rmiClient, 0);
               rmiConnStub.setClientReference(remoteRef);
                authenticator.updateConnected(username);
               return true;
            }
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean loginSocket(){
        return clientConn.login(username,password);
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
            if(shortOption.matches("[hgcrs]")){

                if(options.contains(shortOption)){ throw new IllegalArgumentException(); }
                options.add(shortOption);
            }else if(shortOption.equals("a") && i==option.length()-1){
                if(args.length>index+1 && args[index+1].matches(IP_ADDRESS)){
                    options.add("a");
                    options.add(args[index+1]);
                }else {
                    throw new IllegalArgumentException();
                }
            } else{
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

    private static void setClientPreferences(ArrayList<String> options, Client client) {
        if(options.contains("r")){ client.setConnMode(ConnectionMode.RMI);}
        if(options.contains("s")){ client.setConnMode(ConnectionMode.SOCKET);}
        if(options.contains("g")){ client.setUiMode(UIMode.GUI);}
        if(options.contains("c")){ client.setUiMode(UIMode.CLI);}
        if(options.contains("a")){ client.setServerIP(options.get(options.indexOf("a")+1));}
    }

    public static void main(String[] args){
        ArrayList<String> options;
        Client client = new Client();
        if (args.length>0) {

            try {
                options = (ArrayList<String>) getOptions(args);

            } catch (IllegalArgumentException e) {
                printHelpMessage();
                return;
            }
            if(options.contains("h")){
                printHelpMessage();
            }else {
                setClientPreferences(options, client);

            }
        }

        client.login();


    }


}
