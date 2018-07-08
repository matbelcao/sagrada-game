package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.ClientOptions;
import it.polimi.ingsw.client.connection.ClientConn;
import it.polimi.ingsw.client.connection.RMIClient;
import it.polimi.ingsw.client.connection.RMIClientObject;
import it.polimi.ingsw.client.connection.SocketClient;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.clientUI.CLI;
import it.polimi.ingsw.client.view.clientUI.ClientUI;
import it.polimi.ingsw.client.view.clientUI.GUI;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMode;
import it.polimi.ingsw.common.connection.ClientInt;
import it.polimi.ingsw.common.connection.rmi_interfaces.AuthenticationInt;
import it.polimi.ingsw.common.connection.rmi_interfaces.RMIServerInt;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.common.serializables.*;
import org.fusesource.jansi.AnsiConsole;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents a client that can connect to the server and participate to a match of the game.
 * Every client has some preferences that can be set via command line options (-h to see them)
 */
public class Client implements ClientInt {

    public static final String XML_SOURCE = "xml/client/";

    private static final String EMPTY_STRING="";
    private static final String CONFIGURATION_FILE_NAME= "ClientConf.xml";
    private static final String OS_NAME_PROPERTY = "os.name" ;
    private static final String WINDOWS_OS = "Windows" ;
    private static final String CONFIGURATIONS = "conf";
    private static final String UI ="UI" ;
    private static final String IPV4_ADDRESS = "address";
    private static final String CONNECTION_MODE = "connectionMode";
    private static final String LANG = "language";
    private static final String RMI_PORT ="portRMI";
    private static final String SOCKET_PORT ="portSocket";
    private static final String RMI_SLASHSLASH ="rmi://";
    private static final String SLASH ="/";
    private static final String AUTH = "auth" ;
    public static final String CLIENT_CONF_XML = (new File(Client.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParentFile().getAbsolutePath()
            +File.separator+CONFIGURATION_FILE_NAME;
    public static final int LOGIN_DELAY = 3000;

    private UserStatus userStatus;
    private final Object lockStatus=new Object();

    private ConnectionMode connMode;
    private ClientConn clientConn;
    private String serverIP;
    private Integer port;

    private UIMode uiMode;
    private ClientUI clientUI;
    private UILanguage language;

    private LightBoard board;
    private ClientFSM fsm;

    private String username;
    private char [] password;
    private final Object lockCredentials=new Object();

    private boolean readyWithBasicBoardElems;
    private final Object lockReady = new Object();
    private final List<Thread> updatesQueue;
    private Timer loginTimer;


    /**
     * constructs the client object and sets some parameters
     * @param uiMode the type of ui preferred
     * @param connMode the preferred connection mode
     * @param serverIP the server ip
     * @param port the port to connect to
     * @param language the desired language
     */
    public Client(UIMode uiMode,ConnectionMode connMode,String serverIP,Integer port,UILanguage language) {
        this.uiMode = uiMode;
        this.connMode = connMode;
        this.serverIP = serverIP;
        this.port = port;
        this.language = language;
        this.userStatus = UserStatus.DISCONNECTED;
        this.readyWithBasicBoardElems = false;
        this.updatesQueue =new ArrayList<>();
    }

    /**
     * parses the default settings in the xml file and creates a client based on that
     * @return the newly created client
     */
    private static Client parser(){

        Client  client;
        try (InputStream xmlFile= new FileInputStream(CLIENT_CONF_XML)){
            client=readConfFile(xmlFile);
            if(client!=null){
                return client;
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.INFO,e.getMessage());
            client=null;
        }

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream xmlFile=classLoader.getResourceAsStream(XML_SOURCE + CONFIGURATION_FILE_NAME);
        return readConfFile(xmlFile);

    }

    @NotNull
    private static Client readConfFile(InputStream xmlFile) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            Element eElement = (Element)doc.getElementsByTagName(CONFIGURATIONS).item(0);
            UIMode uiMode=UIMode.valueOf(eElement.getElementsByTagName(UI).item(0).getTextContent());
            String serverIP=eElement.getElementsByTagName(IPV4_ADDRESS).item(0).getTextContent();
            ConnectionMode connMode=ConnectionMode.valueOf(eElement.getElementsByTagName(CONNECTION_MODE).item(0).getTextContent());
            UILanguage lang=UILanguage.getLang(eElement.getElementsByTagName(LANG).item(0).getTextContent());
            int port;
            if(connMode.equals(ConnectionMode.RMI)){
                port=Integer.parseInt(eElement.getElementsByTagName(RMI_PORT).item(0).getTextContent());
            }else{ port=Integer.parseInt(eElement.getElementsByTagName(SOCKET_PORT).item(0).getTextContent()); }

            return new Client(uiMode,connMode,serverIP,port,lang);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            Logger.getGlobal().log(Level.INFO,e.getMessage());
            System.exit(1);
            return null;
        }
    }

    /**
     * @return the state of the client's fsm
     */
    public ClientFSMState getFsmState(){
        return fsm.getState();
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
    public void setUiMode(UIMode uiMode) {
        this.uiMode = uiMode;
    }

    /**
     * This method sets the wanted language
     * @param language the requested language
     */
    public void setLanguage(UILanguage language) {
        this.language = language;
    }

    /**
     * This method sets the wanted connection mode
     * @param connMode the requested connection mode
     */
    public void setConnMode(ConnectionMode connMode) { this.connMode = connMode;
        if(this.connMode.equals(ConnectionMode.RMI)) {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            InputStream xmlFile = classLoader.getResourceAsStream(XML_SOURCE + CONFIGURATION_FILE_NAME);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();
                Element eElement = (Element) doc.getElementsByTagName(CONFIGURATIONS).item(0);
                this.port=Integer.parseInt(eElement.getElementsByTagName(RMI_PORT).item(0).getTextContent());
            }catch (ParserConfigurationException | SAXException | IOException e) {
                Logger.getGlobal().log(Level.INFO,e.getMessage());
            }
        }
    }

    /**
     * this method sets the ip of the server to connect to
     * @param serverIP the ipv4 address of the server
     */
    public void setServerIP(String serverIP) { this.serverIP = serverIP; }

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
            clientUI=new CLI(this, language);
        }else{
            new Thread(() -> GUI.launch(this, language)).start();
            while(GUI.getGUI() == null){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Logger.getGlobal().log(Level.INFO,e.getMessage());
                    System.exit(1);
                }
            }
            clientUI = GUI.getGUI();
        }
        clientUI.showLoginScreen();

    }


    /**
     * @return true iff the client is using windows
     */
    public static boolean isWindows()
    {
        return System.getProperty(OS_NAME_PROPERTY).startsWith(WINDOWS_OS);
    }




    /**
     * This method sets up a connection accordingly to the selected mode and starts the login procedure to gather username
     * and password of the user and try to login to the server.
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
            if(connMode.equals(ConnectionMode.RMI)){
                AuthenticationInt authenticator=(AuthenticationInt) Naming.lookup(RMI_SLASHSLASH+serverIP+":"+port+SLASH+AUTH);
                authenticator.updateConnected(username);
            }

            synchronized (lockStatus) {
                userStatus = UserStatus.LOBBY;
                lockStatus.notifyAll();
            }

            startUICommandController();
            updateMessagesManager();

            clientConn.pong();

        } catch (IOException | NotBoundException e) {
            disconnect();
        }
    }

    /**
     * this logs tries to log the user in
     * @return true iff it succeeded
     */
    private boolean login()  {
        boolean logged=false;
        try {
            if(this.username.equals(EMPTY_STRING)){return false;}

            if (connMode.equals(ConnectionMode.RMI)) {
                logged = loginRMI();
            } else {

                logged = clientConn.login(username, password);
            }
        }catch (Exception e){
            disconnect();
        }
        return logged;
    }

    /**
     * this starts a thread that is going to manage the commands received by the end user through the interaction with the ui
     */
    private void startUICommandController(){
        new UICommandController(fsm,clientUI.getCommandQueue()).start();
    }


    /**
     * This method implements the login to the server via rmi
     * @return true iff the login had a positive result
     */
    private boolean loginRMI() throws RemoteException, MalformedURLException, NotBoundException {
        if(loginTimer==null) {
            loginTimer = new Timer();

            loginTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (lockStatus) {
                        userStatus = UserStatus.CONNECTED;
                    }
                    disconnect();
                }
            }, LOGIN_DELAY);
        }
        AuthenticationInt authenticator=(AuthenticationInt) Naming.lookup(RMI_SLASHSLASH+serverIP+":"+port+SLASH+AUTH);
        if(authenticator.authenticate(username,password)){
            //get the stub of the remote object
            RMIServerInt rmiConnStub = (RMIServerInt) Naming.lookup(RMI_SLASHSLASH+serverIP+":"+port+SLASH+username);
            clientConn = new RMIClient(rmiConnStub, this);
            authenticator.setRemoteReference(new RMIClientObject(this),username);
            clientUI.updateConnectionOk();
            clientUI.updateLogin(true);

            loginTimer.cancel();
            loginTimer=null;
            return true;
        }
        clientUI.updateLogin(false);
        loginTimer.cancel();
        loginTimer=null;
        return false;
    }

    /**
     * @return true iff the user is still connected to the server
     */
    public boolean isLogged(){
        return userStatus.equals(UserStatus.LOBBY)||userStatus.equals(UserStatus.PLAYING);
    }

    void prepareForNewGame() {

        synchronized (lockStatus) {
            this.userStatus = UserStatus.LOBBY;
        }
        synchronized (lockReady) {
            this.readyWithBasicBoardElems = false;
        }
        clientConn.newMatch();
    }


    /**
     * this manages the queue of updates with a fifo logic
     */
    private void updateMessagesManager(){
        new Thread(()-> {
            while(isLogged()) {
                synchronized (updatesQueue) {
                    while (updatesQueue.isEmpty()) {
                        try {
                            updatesQueue.wait();
                        } catch (InterruptedException e) {
                            Logger.getGlobal().log(Level.INFO,e.getMessage());
                            System.exit(2);
                        }
                    }
                    updatesQueue.notifyAll();
                }
                    updatesQueue.get(0).start();
                    try {
                        updatesQueue.get(0).join();
                    } catch (InterruptedException e) {
                        Logger.getGlobal().log(Level.INFO,e.getMessage());
                        System.exit(2);
                    }
                    updatesQueue.remove(0);


            }
        }
        ).start();
    }

    /**
     * this adds a task to the queue of updates to make to the client's view
     * @param newUpdate the new update to be made
     */
    public void addUpdateTask(Thread newUpdate){
        synchronized (updatesQueue){
            updatesQueue.add(newUpdate);
            updatesQueue.notifyAll();
        }
    }

    /**
     * this method signals that a new match is about to begin and
     * @param numPlayers the number of participants
     * @param playerId the id of the user
     */
    public void updateGameStart(int numPlayers, int playerId){

        this.board= new LightBoard(numPlayers);
        synchronized (lockStatus){
            userStatus=UserStatus.PLAYING;
            lockStatus.notifyAll();
        }
        fsm.resetState(true);
        List<LightPlayer> players = clientConn.getPlayers();
        for (int i = 0; i < board.getNumPlayers(); i++) {
            board.addPlayer(players.get(i));
        }

        board.setMyPlayerId(playerId);
        board.setPrivObj(clientConn.getPrivateObject());
        board.setDraftedSchemas(clientConn.getSchemaDraft());
        board.addObserver(clientUI);
        assert(fsm.getState().equals(ClientFSMState.CHOOSE_SCHEMA));

        board.notifyObservers();

    }

    /**
     * this notifies the end of a game be it for disconnection of too many players or for natural causes
     * @param ranking the ranking of the players
     */
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


    /**
     * this notifies the start of a new round
     * @param numRound the number of the new round
     */
    public void updateGameRoundStart(int numRound){

        board.setRoundTrack(clientConn.getRoundtrack());
        board.setRoundNumber(numRound);
        board.setDraftPool(clientConn.getDraftPool());
        fsm.setNotMyTurn();
        board.stateChanged();
        if(numRound==0) {
            synchronized (lockReady) {

                getCardsSchemasFavors();

                readyWithBasicBoardElems =true;
                lockReady.notifyAll();
            }
            return;
        }
        board.notifyObservers();

    }

    /**
     * this method retrieves the players' schemas and favor tokens and the tools and public objectives
     */
    private void getCardsSchemasFavors() {
        for (int i = 0; i < board.getNumPlayers(); i++) {

            board.updateSchema(i, clientConn.getSchema(i));
            board.updateFavorTokens(i,clientConn.getFavorTokens(i));
        }

        board.setTools(clientConn.getTools());
        board.setPubObjs(clientConn.getPublicObjectives());
    }

    /**
     * notifies the end of a round of the game
     * @param numRound the number of the round that just ended
     */
    public void updateGameRoundEnd(int numRound){
        //not implemented
    }

    /**
     * this notifies the user of the start of a new turn
     * @param playerId the player who's about to play
     * @param isFirstTurn true iff this is his/her first turn in the current round
     */
    public void updateGameTurnStart(int playerId, boolean isFirstTurn){

        synchronized (lockReady) {
            while (!readyWithBasicBoardElems) {
                try {
                    lockReady.wait();
                } catch (InterruptedException e) {
                    Logger.getGlobal().log(Level.INFO,e.getMessage());
                    System.exit(1);
                }
            }
        }
        board.setNowPlaying(playerId);
        board.setDraftPool(clientConn.getDraftPool());
        board.setNowPlaying(playerId);
        board.setIsFirstTurn(isFirstTurn);


        fsm.setNotMyTurn();
        fsm.setMyTurn(playerId==board.getMyPlayerId());
        board.stateChanged();

        board.notifyObservers();

    }

    /**
     * this notifies the end of the turn of a player
     * @param playerId the id of the player
     */
    public void updateGameTurnEnd(int playerId){
        board.setTools(clientConn.getTools());
        board.updateSchema(playerId,clientConn.getSchema(playerId));
        board.updateFavorTokens(playerId,clientConn.getFavorTokens(playerId));

        board.notifyObservers();
    }

    /**
     * this method updates the board with the changes regarding the user that is currently playing
     */
    public void getBoardUpdates(){
        board.setDraftPool(clientConn.getDraftPool());
        board.setRoundTrack(clientConn.getRoundtrack());
        board.updateSchema(board.getNowPlaying(),clientConn.getSchema(board.getNowPlaying()));
        board.setTools(clientConn.getTools());
        board.updateFavorTokens(board.getNowPlaying(),clientConn.getFavorTokens(board.getNowPlaying()));

        board.notifyObservers();
    }


    /**
     * this method notifies the update of some player's status in the current game, this also serves the purpose
     * of reconnecting a player to the game he was playing before he lost the connection
     * @param playerId the id of the subject of the change in status
     * @param gameEvent the event in some manner caused by the player
     * @param username the username of the player
     */
    public void updatePlayerStatus(int playerId, GameEvent gameEvent, String username){

        LightPlayerStatus status;

        switch (gameEvent) {
            case QUIT:
                status = LightPlayerStatus.QUITTED;
                break;
            case RECONNECT:
                status = LightPlayerStatus.PLAYING;

                if(username.equals(this.username)){
                    reconnectUserToGame(playerId);
                }
                break;
            case DISCONNECT:
                status = LightPlayerStatus.DISCONNECTED;
                break;
            default:
                status = LightPlayerStatus.PLAYING;
        }
        board.updateStatus(playerId, status);


        board.notifyObservers();

    }

    /**
     * @return true iff the game has started and has not ended yet and the user has chosen his schema
     */
    public boolean isPlayingTurns() {
        return !(fsm.getState().equals(ClientFSMState.SCHEMA_CHOSEN)
                ||fsm.getState().equals(ClientFSMState.CHOOSE_SCHEMA)
                ||fsm.getState().equals(ClientFSMState.GAME_ENDED));
    }

    /**
     * this method implements the rest of the procedure to reconnect a player to the game he lost connection to while
     * he was playing
     * @param playerId his playerID for the game being
     */
    private void reconnectUserToGame(int playerId) {
        synchronized (lockStatus){
            this.userStatus=UserStatus.PLAYING;
            lockStatus.notifyAll();
        }
        retrieveBoardElemsOnReconnection(playerId);

    }

    /**
     * this retrieves the needed board elements for the user that is reconnecting to a game
     * @param myPlayerId his/her playerId
     */
    private void retrieveBoardElemsOnReconnection(int myPlayerId){
        synchronized (lockReady) {
            LightGameStatus gameStatus = clientConn.getGameStatus();

            board = new LightBoard(gameStatus.getNumPlayers());

            board.setMyPlayerId(myPlayerId);

            List<LightPlayer> players = clientConn.getPlayers();

            for (int i = 0; i < board.getNumPlayers(); i++) {
                board.addPlayer(players.get(i));
            }
            board.setPrivObj(clientConn.getPrivateObject());

            if (!gameStatus.isInit()) {
                board.setIsFirstTurn(gameStatus.getIsFirstTurn());
                board.setRoundTrack(clientConn.getRoundtrack());
                board.setRoundNumber(gameStatus.getNumRound());

                board.setNowPlaying(gameStatus.getNowPlaying());

                getCardsSchemasFavors();

                board.setDraftPool(clientConn.getDraftPool());


            } else {
                board.setDraftedSchemas(clientConn.getSchemaDraft());
            }
            board.addObserver(clientUI);
            readyWithBasicBoardElems =true;

            fsm.resetState(gameStatus.isInit());
            board.stateChanged();
            lockReady.notifyAll();
        }
    }


    /**
     * this method quits the player from the game, he/she will not be able to resume the game
     */
    public void quit(){
        if(!userStatus.equals(UserStatus.DISCONNECTED)) {
            clientConn.quit();
        }
        synchronized (lockStatus) {
            userStatus = UserStatus.DISCONNECTED;
            clientUI.updateConnectionClosed();
            System.exit(0);
        }
    }

    /**
     * this method sets the status of the client to disconnected and notifies the user that the connection was found to be broken
     */
    public void disconnect(){
        synchronized (lockStatus) {
            if (!userStatus.equals(UserStatus.DISCONNECTED)) {
                userStatus = UserStatus.DISCONNECTED;
                clientUI.updateConnectionBroken();
            }

        }
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
            if(!ClientOptions.getOptions(args,options) || options.contains(ClientOptions.SHORT_HELP)){
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
            Logger.getGlobal().log(Level.INFO,e.getMessage());
            System.exit(1);
        }

    }



}
