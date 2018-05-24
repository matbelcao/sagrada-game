package it.polimi.ingsw.client;

import it.polimi.ingsw.client.connection.ClientConn;
import it.polimi.ingsw.client.connection.RMIClient;
import it.polimi.ingsw.client.connection.RMIClientInt;
import it.polimi.ingsw.client.connection.SocketClient;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.UIMode;
import it.polimi.ingsw.server.connection.AuthenticationInt;
import it.polimi.ingsw.server.connection.RMIServerInt;
import it.polimi.ingsw.common.enums.UserStatus;
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

/**
 * This class represents a client that can connect to the server and participate to a match of the game.
 * Every client has some preferences that can be set via command line options (-h to see them)
 */
public class Client {

    private UIMode uiMode;
    private ConnectionMode connMode;
    private String username;
    private String password;
    private int personalId;
    private UserStatus userStatus;
    private ClientConn clientConn;
    private String serverIP;
    private Integer port;
    private ClientUI clientUI;
    public static final String XML_SOURCE = "src"+ File.separator+"xml"+File.separator+"client"+ File.separator; //append class name + ".xml" to obtain complete path

    /**
     * this is thee default constructor and it sets the default settings that are loaded from an xml configuration file
     */
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
            this.userStatus = UserStatus.DISCONNECTED;
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * This method constructs the class and overwrites uimode and connmode according to the passed params
     * @param uiMode the wanted UI
     * @param connMode the wanted Connection mode
     */
    public Client(UIMode uiMode,ConnectionMode connMode){
        this();
        this.uiMode=uiMode;
        this.connMode=connMode;
    }

    /**
     * This method constructs the class and overwrites uimode and connmode according to the passed params
     * @param uiMode the wanted UI
     * @param connMode the wanted Connection mode
     */
    public Client(String uiMode,String connMode){
        this();
        this.uiMode=UIMode.valueOf(uiMode);
        this.connMode=ConnectionMode.valueOf(connMode);
    }

    /**
     * This method sets the wanted uimode
     * @param uiMode the requested UI
     */
    void setUiMode(UIMode uiMode) {
        this.uiMode = uiMode;
    }

    /**
     * This method sets the wanted connection mode
     * @param connMode the requested connection mode
     */
    void setConnMode(ConnectionMode connMode) { this.connMode = connMode; }

    /**
     * this method sets the ip of the server to connect to
     * @param serverIP the ipv4 address of the server
     */
    public void setServerIP(String serverIP) { this.serverIP = serverIP; }

    /**
     * this sets the username of the client
     * @param username the username to be set
     */
    public void setUsername(String username){ this.username = username; }

    /**
     * this sets the password of the client (this will be used to try and login and may differ from the one in the server)
     * @param password the password to be set
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * @return the set username
     */
    public String getUsername() { return username; }

    /**
     * @return the set password
     */
    public String getPassword(){ return password; }

    /**
     * @return the object that is the connection of the client towards the server
     */
    public ClientConn getClientConn(){ return clientConn; }

    /**
     * @return the object that is the UI of the client
     */
    public ClientUI getClientUI(){return clientUI;}


    /**
     * This method launches a thread that periodically checks if the user is correctly connected towards the server
     */
    public void heartbeat(){
        new Thread(() -> {
            while(!userStatus.equals(UserStatus.DISCONNECTED)) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                if(!clientConn.ping()){this.disconnect();}
            }
        }).start();
    }


    /**
     * This method instantiates the user interface
     */
    private void setup(){
        if (uiMode.equals(UIMode.CLI)){
            clientUI=new CLI(this);
        }else{
            System.out.println("Launching GUI (still not implemented....");
            clientUI=new GUI(this);
        }
    }


    /**
     * This method sets up a connection accordingly to the selected mode and starts the login procedure to gather username and password of the user and try to login to the server.
     * If the login is successful the client will be put in the lobby where he will wait for the beginning of a new match
     */
    private void coonectAndLogin(){
        boolean logged=false;

        try {
            userStatus = UserStatus.CONNECTED;
            if (connMode.equals(ConnectionMode.SOCKET)) {
                clientConn = new SocketClient(this, serverIP, port);
                heartbeat();
            }
            do {
                clientUI.loginProcedure();
                if (connMode.equals(ConnectionMode.RMI)) {
                    logged = loginRMI();
                } else {
                    logged = clientConn.login(username, password);
                }
            } while (!logged);
            userStatus = UserStatus.LOBBY;
        }catch(Exception e){
            userStatus=UserStatus.DISCONNECTED;
            clientUI.updateConnectionBroken();
        }
    }

    /**
     * This method implements the login to the server via rmi
     * @return true iff the login had a positive result
     */
    private boolean loginRMI() throws RemoteException, MalformedURLException, NotBoundException {
            AuthenticationInt authenticator=(AuthenticationInt) Naming.lookup("rmi://"+serverIP+"/auth");
            if(authenticator.authenticate(username,password)){
                //get the stub of the remote object
                RMIServerInt rmiConnStub = (RMIServerInt) Naming.lookup("rmi://"+serverIP+"/"+username+password);
                //create RMIClient with the reference of the remote obj and assign it to the Client
                RMIClientInt rmiClient  = new RMIClient(rmiConnStub,this);
                clientConn = (RMIClient)rmiClient;
                //create a remote reference of the obj rmiClient and pass it to the server.
                //a remote reference is passed so there's no need to add rmiClient to a Registry
                RMIClientInt remoteRef = (RMIClientInt) UnicastRemoteObject.exportObject(rmiClient, 0);
                rmiConnStub.setClientReference(remoteRef);
                clientUI.updateConnectionOk();
                clientUI.updateLogin(true);
                authenticator.updateConnected(username);
                return true;
            }
        clientUI.updateLogin(false);
        return false;
    }

    /**
     * This method provides the lobby functionality. It remains in execution until the player is assigned to a match or decides to quit
     */
    private void lobby(){
        while(userStatus.equals(UserStatus.LOBBY)) {
            if (clientUI.getCommand().equals("QUIT")){
                clientUI.printmsg(clientConn.getPrivateObj().toString());
                quit();
            }
        }
    }

    public void updateGameStart(int numPlayers, int personalId){
        clientUI.updateGameStart(numPlayers,personalId);
        this.personalId=personalId;
        userStatus=UserStatus.PLAYING;
    }



    private void match(){
        while(userStatus.equals(UserStatus.PLAYING)) {
            if (clientUI.getCommand().equals("QUIT")){
                quit();
            }
        }
    }



    public void quit(){
        clientConn.quit();
        userStatus=UserStatus.DISCONNECTED;
        clientUI.updateConnectionClosed();
    }

    public void disconnect(){
        userStatus=UserStatus.DISCONNECTED;
        clientUI.updateConnectionBroken();
    }


    public static void main(String[] args){
        ArrayList<String> options=new ArrayList<>();
        Client client = new Client();
        if (args.length>0) {
            if(!ClientOptions.getOptions(args,options) || options.contains("h")){
                ClientOptions.printHelpMessage();
            }else {
                ClientOptions.setClientPreferences(options, client);
            }
        }

        client.setup();
        client.coonectAndLogin();
        client.lobby();
        client.match();
    }


}
