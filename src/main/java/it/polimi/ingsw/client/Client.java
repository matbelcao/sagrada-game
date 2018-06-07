package it.polimi.ingsw.client;

import it.polimi.ingsw.client.connection.ClientConn;
import it.polimi.ingsw.client.connection.RMIClient;
import it.polimi.ingsw.client.connection.RMIClientInt;
import it.polimi.ingsw.client.connection.SocketClient;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.UIMode;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.server.connection.AuthenticationInt;
import it.polimi.ingsw.server.connection.RMIServerInt;
import it.polimi.ingsw.server.connection.RMIServerObject;
import org.fusesource.jansi.AnsiConsole;
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
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a client that can connect to the server and participate to a match of the game.
 * Every client has some preferences that can be set via command line options (-h to see them)
 */
public class Client {

    private UIMode uiMode;
    private ConnectionMode connMode;
    private String username;
    private char [] password;

    private UserStatus userStatus;
    private final Object lockStatus=new Object();
    private ClientConn clientConn;
    private String serverIP;
    private Integer port;
    private ClientUI clientUI;
    private UILanguage lang;
    private LightBoard board;
    private final Object lockState=new Object();
    private ClientFSMState turnState;
    public static final String XML_SOURCE = "src"+ File.separator+"xml"+File.separator+"client" +File.separator;
    private final Object lockCredentials=new Object();


    public static boolean isWindows()
    {
        return System.getProperty("os.name").startsWith("Windows");
    }


    /**
     * constructs the client object and sets some parameters
     * @param uiMode the type of ui preferred
     * @param connMode the preferred connection mode
     * @param serverIP the server ip
     * @param port the port to connect to
     * @param lang the desired language
     */
    public Client(UIMode uiMode,ConnectionMode connMode,String serverIP,Integer port,UILanguage lang) {
        this.uiMode = uiMode;
        this.connMode = connMode;
        this.serverIP = serverIP;
        this.port = port;
        this.lang = lang;
        this.userStatus = UserStatus.DISCONNECTED;

    }


    /**
     * parses the default settings in the xml file and creates a client based on that
     * @return the newly created client
     */
    private static Client parser(){
        File xmlFile= new File(XML_SOURCE+"ClientConf.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            Element eElement = (Element)doc.getElementsByTagName("conf").item(0);
            UIMode uiMode=UIMode.valueOf(eElement.getElementsByTagName("UI").item(0).getTextContent());
            String serverIP=eElement.getElementsByTagName("address").item(0).getTextContent();
            ConnectionMode connMode=ConnectionMode.valueOf(eElement.getElementsByTagName("connectionMode").item(0).getTextContent());
            UILanguage lang=UILanguage.valueOf(eElement.getElementsByTagName("language").item(0).getTextContent());
            int port;
            if(connMode.equals(ConnectionMode.RMI)){
                port=Integer.parseInt(eElement.getElementsByTagName("portRMI").item(0).getTextContent());
            }else{ port=Integer.parseInt(eElement.getElementsByTagName("portSocket").item(0).getTextContent()); }

            return new Client(uiMode,connMode,serverIP,port,lang);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.exit(1);
            return null;
        }


    }

    public static Client getNewClient() throws InstantiationException {
        Client newClient = parser();
        if(newClient==null){
            throw new InstantiationException();
        }
        return newClient;
    }

    public Object getLockCredentials(){ return lockCredentials;  }

    public int getPlayerId() {
        return board.getMyPlayerId();
    }



    public LightBoard getBoard() {
        return board;
    }

    /**
     * This method sets the wanted uimode
     * @param uiMode the requested UI
     */
    void setUiMode(UIMode uiMode) {
        this.uiMode = uiMode;
    }

    /**
     * @return the set language
     */
    public UILanguage getLang() {
        return lang;
    }

    /**
     * This method sets the wanted language
     * @param lang the requested language
     */
    public void setLang(UILanguage lang) {
        this.lang = lang;
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
    public void setPassword(char[] password) { this.password = password; }

    /**
     * @return the set username
     */
    public String getUsername() { return username; }

    public void setUserStatus(UserStatus status){
        this.userStatus=status;
    }

    public ConnectionMode getConnMode() {
        return connMode;
    }

    /**
     * @return the object that is the connection of the client towards the server
     */
    public ClientConn getClientConn(){ return clientConn; }

    /**
     * @return the object that is the UI of the client
     */
    public ClientUI getClientUI(){return clientUI;}

    /**
     * This method instantiates the user interface
     */
    private void setupUI() {
        if (uiMode.equals(UIMode.CLI)){
            if(isWindows()){
                AnsiConsole.systemInstall();
            }
            clientUI=new CLI(this,lang);

            clientUI.showLoginScreen();
        }else{
            System.out.println("Launching GUI (still not implemented....");
            new Thread(() -> GUI.launch(this,lang)).start();
            while(GUI.getGUI() == null){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            clientUI = GUI.getGUI();
        }
    }


    /**
     * This method sets up a connection accordingly to the selected mode and starts the login procedure to gather username and password of the user and try to login to the server.
     * If the login is successful the client will be put in the lobby where he will wait for the beginning of a new match
     */
    private void connectAndLogin() throws InterruptedException {
        boolean logged=false;
        synchronized (lockStatus) {
            userStatus = UserStatus.CONNECTED;
            lockStatus.notifyAll();
        }
        try {

            if (connMode.equals(ConnectionMode.SOCKET)) {
                clientConn = new SocketClient(this, serverIP, port);
            }
            do {

                synchronized (lockCredentials) {
                    while (username == null || password == null) {
                        lockCredentials.wait();
                    }
                    lockCredentials.notifyAll();
                }

                if (connMode.equals(ConnectionMode.RMI)) {
                    logged = loginRMI();
                } else {
                    logged = clientConn.login(username, password);
                }

                synchronized (lockCredentials) {
                    Arrays.fill(this.password, ' ');
                    lockCredentials.notifyAll();
                }

                if(!logged){
                    synchronized (lockCredentials) {
                        username = null;
                        password = null;
                        lockCredentials.notifyAll();
                    }
                }

            } while (!logged);


            synchronized (lockStatus) {
                userStatus = UserStatus.LOBBY;
                lockStatus.notifyAll();
            }

        } catch (IOException | NotBoundException e) {
            synchronized (lockStatus) {
                userStatus = UserStatus.DISCONNECTED;
                lockStatus.notifyAll();
            }
            clientUI.updateConnectionBroken();
        }
    }

    /**
     * This method implements the login to the server via rmi
     * @return true iff the login had a positive result
     */
    private boolean loginRMI() throws RemoteException, MalformedURLException, NotBoundException {
        System.setProperty("java.rmi.server.hostname",serverIP);
        AuthenticationInt authenticator=(AuthenticationInt) Naming.lookup("rmi://"+serverIP+"/auth");
        if(authenticator.authenticate(username,password)){
            //get the stub of the remote object
            RMIClientInt rmiConnStub = (RMIClientInt) Naming.lookup("rmi://"+serverIP+"/"+username);
            clientConn = new RMIClient(rmiConnStub, this);
            RMIServerInt rmiServerObject = new RMIServerObject(this);
            //check if the method is necessary or just extend unicast remote obj in RMIServerObject
            RMIServerInt remoteRef = (RMIServerInt) UnicastRemoteObject.exportObject(rmiServerObject, 0);
            authenticator.setRemoteReference(remoteRef,username);
            clientUI.updateConnectionOk();
            clientUI.updateLogin(true);
            authenticator.updateConnected(username);
            return true;
        }
        clientUI.updateLogin(false);
        return false;
    }


    public boolean isLogged(){
        return userStatus.equals(UserStatus.LOBBY)||userStatus.equals(UserStatus.PLAYING);
    }

    /**
     * this method signals that a new match is about to begin and
     * @param numPlayers the number of participants
     * @param playerId the id of the user
     */
    public void updateGameStart(int numPlayers, int playerId){
        this.board= new LightBoard(numPlayers);
        board.addObserver(clientUI);
        clientUI.updateGameStart(numPlayers,playerId);
        board.setMyPlayerId(playerId);

        synchronized (lockStatus){
            userStatus=UserStatus.PLAYING;
            lockStatus.notifyAll();
        }
        board.setPrivObj(clientConn.getPrivateObject());
        clientUI.showDraftedSchemas(clientConn.getSchemaDraft(),board.getPrivObj());

        synchronized (lockState) {
            this.turnState = ClientFSMState.CHOOSING_SCHEMA;
            lockState.notifyAll();
        }
    }

    public void updateGameEnd(){
        //lettura lista di player e classifica
    }

    public void updateGameRoundStart(int numRound){

        if(numRound==0){
            synchronized (lockState){
                assert(turnState.equals(ClientFSMState.CHOOSING_SCHEMA));
                turnState = turnState.nextState(true,false,false,false);
            }
            //get players
            List<LightPlayer> players= clientConn.getPlayers();
            for(int i=0; i<board.getNumPlayers();i++){

                board.addPlayer(players.get(i));

                //get players schema
                board.updateSchema(i,clientConn.getSchema(i));

                //set favor tokens
                board.getPlayerByIndex(i).setFavorTokens(clientConn.getFavorTokens(i));
            }
            players=null;

            //get tools
            board.addTools(clientConn.getTools());

            List<LightCard> pubObj= clientConn.getPublicObjects();
            //get public objectives
            for(int i=0; i< LightBoard.NUM_PUB_OBJ;i++){
                board.addPubObj(pubObj.get(i));
            }
            clientUI.showTurnInitScreen();
        }

        board.notifyObservers();
        
    }

    public void updateGameRoundEnd(int numRound){
        board.setRoundTrack(clientConn.getRoundtrack(),numRound+1);

        board.notifyObservers();
    }

    public void updateGameTurnStart(int playerId, boolean isFirstTurn){

        board.setDraftPool(clientConn.getDraftPool());
        board.setNowPlaying(playerId);
        board.setIsFirstTurn(isFirstTurn);
        board.setRoundTrack(clientConn.getRoundtrack(),board.getRoundNumber());

        synchronized (lockState) {
            turnState=turnState.nextState(playerId == board.getMyPlayerId(), false, false, false);
            lockState.notifyAll();
        }


        board.notifyObservers();
    }

    public void updateGameTurnEnd(int playerTurnId, int firstOrSecond){
        board.updateSchema(playerTurnId,clientConn.getSchema(playerTurnId));
        board.getPlayerByIndex(playerTurnId).setFavorTokens(clientConn.getFavorTokens(playerTurnId));
        for (int i=0; i<LightBoard.NUM_TOOLS; i++) {
            if(!board.getTools().get(i).isUsed()){
                board.getTools().set(i,clientConn.getTools().get(i));
            }
        }
        synchronized (lockState) {
            turnState=turnState.nextState(false,false,playerTurnId==getPlayerId(),false);
            lockState.notifyAll();
        }

        board.notifyObservers();

    }

    public void updatePlayerStatus(int playerId, UserStatus status){
        
    }



    //ONLY FOR DEBUG PURPOSES
    public void printDebug(String message){
        clientUI.printmsg(message);
    }

    /**
     * this method quits the player from the game, he/she will not be able to resume the game
     */
    public void quit(){
        clientConn.quit();
        synchronized (lockStatus) {
            userStatus = UserStatus.DISCONNECTED;
            lockStatus.notifyAll();
        }
        clientUI.updateConnectionClosed();
    }

    public void disconnect(){
        synchronized (lockStatus) {
            userStatus = UserStatus.DISCONNECTED;
            lockStatus.notifyAll();
        }
        clientUI.updateConnectionBroken();
    }

    public static void main(String[] args){
        ArrayList<String> options=new ArrayList<>();
        Client client = null;
        try {
            client = Client.getNewClient();
        } catch (InstantiationException e) {
            System.err.println("\u001B[31m"+"ERR: couldn't start the Client"+"\u001B[0m");

            System.exit(1);
        }
        if (args.length>0) {
            if(!ClientOptions.getOptions(args,options) || options.contains("h")){
                ClientOptions.printHelpMessage();
                return;
            }else {
                ClientOptions.setClientPreferences(options, client);
            }
        }

        client.setupUI();
        try {
            client.connectAndLogin();
        } catch (InterruptedException e) {
            System.err.println("ERR: connectAndLogin method interrupted");
            System.exit(1);
        }

    }
}
