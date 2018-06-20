package it.polimi.ingsw.client;

import it.polimi.ingsw.client.clientFSM.ClientFSMState;
import it.polimi.ingsw.client.view.clientUI.CLI;
import it.polimi.ingsw.client.view.clientUI.ClientUI;
import it.polimi.ingsw.client.connection.ClientConn;
import it.polimi.ingsw.client.connection.RMIClient;
import it.polimi.ingsw.client.connection.RMIClientInt;
import it.polimi.ingsw.client.connection.SocketClient;
import it.polimi.ingsw.client.view.clientUI.GUI;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMode;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.common.serializables.*;
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
import java.util.List;

import static it.polimi.ingsw.common.enums.ErrMsg.*;

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
    private ClientFSM fsm;
    public static final String XML_SOURCE = "src"+ File.separator+"xml"+File.separator+"client" +File.separator;
    private final Object lockCredentials=new Object();

    private boolean ready;
    private final Object lockReady = new Object();


    /**
     * @return true iff the client is using windows
     */
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
        this.ready = false;
    }

    void reset() {
        synchronized (lockStatus) {
            this.userStatus = UserStatus.DISCONNECTED;
        }


        synchronized (lockStatus) {
            this.userStatus = UserStatus.CONNECTED;
        }
        synchronized (lockReady) {
            this.ready = false;
        }

        clientConn.newMatch();
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

    /**
     * @return a new Client object
     */
    private static Client getNewClient() {
        return parser();
    }

    /**
     * @return the client's light version of the board
     */
    LightBoard getBoard() {
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
    void setLang(UILanguage lang) {
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
    void setServerIP(String serverIP) { this.serverIP = serverIP; }

    /**
     * this sets the username of the client
     * @param username the username to be set
     */
    public void setUsername(String username){
        synchronized (lockCredentials){
            this.username = username;
            lockCredentials.notifyAll();
        }
    }

    /**
     * this sets the password of the client (this will be used to try and login and may differ from the one in the server)
     * @param password the password to be set
     */
    public void setPassword(char[] password) {
        synchronized (lockCredentials) {
            this.password = password;
            lockCredentials.notifyAll();
        }
    }

    /**
     * @return the set username
     */
    public String getUsername() { return username; }

    /**
     * @return the connection mode of the user
     */
    public ConnectionMode getConnMode() {
        return connMode;
    }

    /**
     * @return the object that is the connection of the client towards the server
     */
    ClientConn getClientConn(){ return clientConn; }

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
        }else{
            new Thread(() -> GUI.launch(this,lang)).start();
            while(GUI.getGUI() == null){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            clientUI = GUI.getGUI();
        }
        clientUI.showLoginScreen();

    }




    /**
     * This method sets up a connection accordingly to the selected mode and starts the login procedure to gather username and password of the user and try to login to the server.
     * If the login is successful the client will be put in the lobby where he will wait for the beginning of a new match
     */
    private void connectAndLogin() throws InterruptedException {
        boolean logged;
        synchronized (lockStatus) {
            userStatus = UserStatus.CONNECTED;
            lockStatus.notifyAll();
        }
        try {

            if (connMode.equals(ConnectionMode.SOCKET)) {
                clientConn = new SocketClient(this, serverIP, port);
            }
            do{
                
                synchronized (lockCredentials) {
                    while (username == null || password == null) {
                        lockCredentials.wait();
                    }
                }

                logged = login();


                if(!logged) {
                    synchronized (lockCredentials) {
                        username = null;
                        password = null;
                        lockCredentials.notifyAll();
                    }
                }

                clientUI.updateLogin(logged);

            }while(!logged);

            this.fsm=new ClientFSM(this);
            //start collecting commands from ui
            commandManager();

            synchronized (lockStatus) {
                userStatus = UserStatus.LOBBY;
                lockStatus.notifyAll();
            }

            //ENABLE PONG
            clientConn.pong();

        } catch (IOException | NotBoundException e) {
            synchronized (lockStatus) {
                userStatus = UserStatus.DISCONNECTED;
                lockStatus.notifyAll();
            }
            clientUI.updateConnectionBroken();
        }
    }

    private boolean login() throws RemoteException, MalformedURLException, NotBoundException {
        boolean logged;
        if (connMode.equals(ConnectionMode.RMI)) {
            logged = loginRMI();
        } else {
            logged = clientConn.login(username, password);
        }
        return logged;
    }

    private void commandManager(){

        new UICommandController(fsm,clientUI.getCommandQueue()).start();

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

    /**
     * @return true iff the user is still connected to the server
     */
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

        List<LightPlayer> players = clientConn.getPlayers();
        for (int i = 0; i < board.getNumPlayers(); i++) {
            board.addPlayer(players.get(i));
        }

        board.setMyPlayerId(playerId);

        synchronized (lockStatus){
            userStatus=UserStatus.PLAYING;
            lockStatus.notifyAll();
        }

        board.setPrivObj(clientConn.getPrivateObject());

        board.setDraftedSchemas(clientConn.getSchemaDraft());

        clientUI.updateGameStart(numPlayers,playerId);

        clientUI.showDraftedSchemas(board.getDraftedSchemas(),board.getPrivObj());



    }

    public void updateGameEnd(List<RankingEntry> ranking){
        for(RankingEntry entry : ranking){
            LightPlayer player=board.getPlayerById(entry.getPlayerId());
            player.setFinalPosition(entry.getFinalPosition());
            player.setPoints(entry.getPoints());
        }
        fsm.endGame();
        board.stateChanged();
        board.notifyObservers();
    }

    public void updateGameRoundStart(int numRound){
        board.setRoundTrack(clientConn.getRoundtrack(),numRound);
        if(numRound==0) {
            //get players
            synchronized (lockReady) {
                for (int i = 0; i < board.getNumPlayers(); i++) {

                    //get players schema
                    board.updateSchema(i, clientConn.getSchema(i));

                    //set favor tokens
                    board.updateFavorTokens(i,clientConn.getFavorTokens(i));
                }
                //get tools
                board.setTools(clientConn.getTools());

                board.setPubObjs(clientConn.getPublicObjects());


                ready=true;
                lockReady.notifyAll();
            }

        }


    }

    public void updateGameRoundEnd(int numRound){

    }

    public void updateGameTurnStart(int playerId, boolean isFirstTurn){

        synchronized (lockReady) {
            while (!ready) {
                try {
                    lockReady.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.err.println(ERR.toString()+INTERRUPTED_READY_WAIT);
                    System.exit(2);
                }
            }
        }
        board.setDraftPool(clientConn.getDraftPool());
        board.setNowPlaying(playerId);
        board.setIsFirstTurn(isFirstTurn);
        board.setRoundTrack(clientConn.getRoundtrack(), board.getRoundNumber());

        fsm.setNotMyTurn();
        fsm.setMyTurn(playerId==board.getMyPlayerId());


        board.notifyObservers();

    }

    public void updateGameTurnEnd(int playerTurnId){
        board.updateSchema(playerTurnId,clientConn.getSchema(playerTurnId));

        board.notifyObservers();


    }


    public void updatePlayerStatus(int playerId, Event event){
        LightPlayerStatus status;
        switch (event){
            case QUIT:
                status=LightPlayerStatus.QUITTED;
                break;
            case RECONNECT:
                status=LightPlayerStatus.PLAYING;
                break;
            case DISCONNECT:
                status=LightPlayerStatus.DISCONNECTED;
                break;
            default:
                status=LightPlayerStatus.PLAYING;
        }

        board.updatestatus(playerId, status);
        board.notifyObservers();
    }

    /**
     * this method updates the board with the changes regarding the user that is currently playing
     */
    public void getUpdates(){

        board.setDraftPool(clientConn.getDraftPool());
        board.setRoundTrack(clientConn.getRoundtrack(), board.getRoundNumber());
        board.updateSchema(board.getNowPlaying(),clientConn.getSchema(board.getNowPlaying()));
        board.setTools(clientConn.getTools());
        board.updateFavorTokens(board.getNowPlaying(),clientConn.getFavorTokens(board.getNowPlaying()));
        board.notifyObservers();
    }


    /**
     * @return the state of the client's fsm
     */
    public ClientFSMState getTurnState(){
        return fsm.getState();
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
        System.exit(0);
    }

    /**
     * this method sets the status of the client to disconnected and notifies the user that the connection was found to be broken
     */
    public void disconnect(){
        synchronized (lockStatus) {
            userStatus = UserStatus.DISCONNECTED;
            lockStatus.notifyAll();
        }
        clientUI.updateConnectionBroken();
        System.exit(0);
    }

    /**
     * the main method for the client
     * @param args the arguments coming from the commandline
     */
    public static void main(String[] args){
        ArrayList<String> options=new ArrayList<>();
        Client client;
        client = Client.getNewClient();
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
            System.err.println(ERR.toString() + INTERRUPTED_LOGIN_PROCEDURE.toString());
            System.exit(2);
        }

    }



}
