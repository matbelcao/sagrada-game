package it.polimi.ingsw.client;

import it.polimi.ingsw.client.connection.ClientConn;
import it.polimi.ingsw.client.connection.RMIClient;
import it.polimi.ingsw.client.connection.RMIClientInt;
import it.polimi.ingsw.client.connection.SocketClient;
import it.polimi.ingsw.client.exceptions.GameStartedException;
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

public class Client {

    private UIMode uiMode;
    private ConnectionMode connMode;
    private String username;
    private String password;
    private UserStatus userStatus;
    private ClientConn clientConn;
    private String serverIP;
    private Integer port;
    private ClientUI clientUI;
    public static final String XML_SOURCE = "src"+ File.separator+"xml"+File.separator+"client"+ File.separator; //append class name + ".xml" to obtain complete path

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

    public void printMessage(String s){
        clientUI.printmsg(s);
    }

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

    public void setupConnection(){
        if(connMode.equals(ConnectionMode.SOCKET)) {
            clientConn = new SocketClient(this,serverIP, port);
            clientUI.updateConnection(); //not correct for RMI, the connection can only  be established after login
        }
    }



    public void setConnection(ClientConn clientConn){
        this.clientConn = clientConn;
    }
    public ClientConn getClientConn(){
        return clientConn;
    }

    public ClientUI getClientUI(){return clientUI;}

    public UIMode getUiMode() {
        return uiMode;
    }


    private void setupAndLogin(){
        boolean logged;

        if (uiMode==UIMode.CLI){
            clientUI=new CLI(this);
        }else{
            System.out.println("Launchin GUI (not again implemented....");
            //clientUI=new GUI(this);
        }
        setupConnection();
        userStatus=UserStatus.CONNECTED;
        if(connMode.equals(ConnectionMode.SOCKET)){ heartbeat();}
        do{
            clientUI.loginProcedure();
            if(connMode.equals(ConnectionMode.RMI)){
                logged=loginRMI();
            }else{
                logged=loginSocket();
            }
            clientUI.updateLogin(logged);
        }while(!logged);
        userStatus=UserStatus.LOBBY;
    }

    private void lobby(){
        int lobbySize=0;
        while(userStatus.equals(UserStatus.LOBBY)) {
            try {
                if(connMode.equals(ConnectionMode.SOCKET)){
                    clientUI.updateLobby(clientConn.getLobby());
                }else if(lobbySize!=clientConn.getLobby()){
                    lobbySize=clientConn.getLobby();
                    clientUI.updateLobby(lobbySize);
                }
            } catch (GameStartedException e) {
                clientUI.updateGameStart(e.getNumPlayers(),e.getPlayerId());
                userStatus = UserStatus.PLAYING;
            }
        }
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

        client.setupAndLogin();
        client.lobby();
        client.match();
    }


}
