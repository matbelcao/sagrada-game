package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.connection.interfacesrmi.AuthenticationInt;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.server.ServerOptions;
import it.polimi.ingsw.server.connection.RMIAuthenticator;
import it.polimi.ingsw.server.connection.SocketAuthenticator;
import org.jetbrains.annotations.Nullable;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class is the server, it handles the login of the clients and the beginning of matches
 */
public class MasterServer{
    private static final String CONFIGURATION_FILE_NAME="ServerConf.xml";
    private static final String SERVER_CONF_XML =
            (new File(MasterServer.class.getProtectionDomain().getCodeSource().getLocation().getPath())).getParentFile().getAbsolutePath()
            +File.separator+CONFIGURATION_FILE_NAME;
    private static final String CONF = "conf";
    private static final String IPV4_ADDRESS = "address";
    private static final String PORT_RMI = "portRMI";
    private static final String PORT_SOCKET = "portSocket";
    private static final String TIME_LOBBY = "timeLobby";
    private static final String TIME_GAME = "timeGame";
    private static final String ADDITIONAL_SCHEMAS = "additionalSchemas";
    private static final String STARTING_MASTER_SERVER = "--> STARTING :  Master Server";
    private static final String TIMER_STARTING = "TIMER STARTING ";
    private static final String SEC = " sec";
    private static final String SERVER_STARTING_CONNECTIONS_VIA_RMI = "--> SERVER STARTING CONNECTIONS VIA RMI";
    private static final String JAVA_RMI_SERVER_HOSTNAME = "java.rmi.server.hostname";
    private static final String RMI_SLASHSLASH = "rmi://";
    private static final String AUTH = "/auth";
    private static final String SERVER_STARTING_CONNECTIONS_VIA_SOCKET = "--> SERVER STARTING CONNECTIONS VIA SOCKET";
    private static final String NEW_CONNECTION_ESTABLISHED_VIA_SOCKET = "New connection established via Socket!";
    private static final String LOGGED = "Logged : ";
    private static final String WRONG_PASS = "Wrong password : ";
    private static final String USER_ALREADY_LOGGED_IN = "User already logged in : ";
    private static final String ERR_STARTING_SOCKET = "ERR: starting SOCKET";
    private static final String ERR_START_MASTER_SERVER = "ERR: couldn't start the Master Server";
    private static final String ERR_STARTING_RMI = "ERR: starting rmi";
    private static MasterServer instance;
    private String ipAddress;
    private int portRMI;
    private int portSocket;

    private static Logger logger= Logger.getGlobal();

    private boolean additionalSchemas; //to be used for additional schemas FA
    public static final String XML_SOURCE = "xml/server/"; //append class name + ".xml" to obtain complete path
    private int lobbyTime;
    private int turnTime;
    private final ArrayList <User> users;
    private final ArrayList <User> lobby;
    private final ArrayList <Game> games;
    private static final int MIN_PLAYERS=2;
    private static final int MAX_PLAYERS=4;
    private final Object lockGames= new Object();

    /**
     * This is the constructor of the server, it initializes the address of the port for socket and rmi connection,
     * it's made private as MasterServer is a Singleton
     */
    private MasterServer(String ipAddress, int portSocket,int portRMI, boolean additionalSchemas, int lobbyTime, int turnTime) {
        this.ipAddress = ipAddress;
        this.portSocket = portSocket;
        this.portRMI = portRMI;
        this.additionalSchemas = additionalSchemas;
        this.lobbyTime = lobbyTime;
        this.turnTime = turnTime;
        users = new ArrayList<>();
        lobby = new ArrayList<>();
        games = new ArrayList<>();
        printMessage(STARTING_MASTER_SERVER);
        new LobbyHandler();
    }

    /**
     * parses the xml configuration file
     * @return a new masterserver instance with the parsed configurations
     */
    private static MasterServer parser(){
        MasterServer master;
        try (InputStream xmlFile= new FileInputStream(SERVER_CONF_XML)){
            master=readConfFile(xmlFile);
            if(master!=null){
                return master;
            }
        } catch (IOException e) {
            master=null;
        }

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream xmlFile=classLoader.getResourceAsStream(XML_SOURCE + CONFIGURATION_FILE_NAME);
        return readConfFile(xmlFile);
    }

    /**
     * this reads the xml file
     * @param xmlFile the xml file to be read
     * @return the new master server instance (could be null if the configuration file was corrupted or non existent)
     */
    @Nullable
    private static MasterServer readConfFile(InputStream xmlFile) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element eElement = (Element)doc.getElementsByTagName(CONF).item(0);
            String setipAddress=eElement.getElementsByTagName(IPV4_ADDRESS).item(0).getTextContent();
            int setportRMI=Integer.parseInt(eElement.getElementsByTagName(PORT_RMI).item(0).getTextContent());
            int setportSocket=Integer.parseInt(eElement.getElementsByTagName(PORT_SOCKET).item(0).getTextContent());
            int setlobbyTime=Integer.parseInt(eElement.getElementsByTagName(TIME_LOBBY).item(0).getTextContent());
            int setturnTime=Integer.parseInt(eElement.getElementsByTagName(TIME_GAME).item(0).getTextContent());
            boolean setadditionalSchemas=Boolean.parseBoolean(eElement.getElementsByTagName(ADDITIONAL_SCHEMAS).item(0).getTextContent());
            return new MasterServer(setipAddress,setportSocket,setportRMI,setadditionalSchemas,setlobbyTime,setturnTime);
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            logger.log(Level.INFO,e1.getMessage());
            return null;
        }
    }

    /**
     * @return the port to "connect" to the server via rmi
     */
    public int getRMIPort() {
        return portRMI;
    }

    /**
     * @return the port on which the server is waiting for socket connections
     */
    public int getSocketPort() {
        return portSocket;
    }

    /**
     * sets the ip address to be listening on
     * @param ipAddress the ipv4 address
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * sets whether or not to show the additional schemas
     * @param additionalSchemas true iff they are to be used
     */
    public void setAdditionalSchemas(boolean additionalSchemas) {
        this.additionalSchemas = additionalSchemas;
    }

    /**
     * this sets the desired time for the lobby timer
     * @param lobbyTime the desired time in seconds
     */
    public void setLobbyTime(int lobbyTime) {
        this.lobbyTime = lobbyTime;
    }

    /**
     * this sets the desired time for the turn timer
     * @param turnTime the desired time in seconds
     */
    public void setTurnTime(int turnTime ) {
        this.turnTime = turnTime;
    }

    /**
     * This is the getter of the MasterServer
     * @return the instance of the MasterServer
     */
    public static MasterServer getMasterServer()  {
        if (instance == null) instance = parser();
        return instance;
    }

    /**
     * @return the max time a player has to play his turn
     */
    int getTurnTime() {
        return turnTime;
    }

    /**
     * this handles the timer for the lobby
     */
    private static class LobbyHandler extends TimerTask {
        @Override
        public void run(){
            MasterServer masterServer=getMasterServer();
            if(masterServer!=null) {
                masterServer.updateLobby();
            }
        }
    }

    /**
     * Updates the lobby queue and instantiate the new Games
     */
    private void updateLobby() {

        ArrayList<User> players = new ArrayList<>();
        boolean lobbyChanged = false;
        Game game;
        synchronized (lobby) {

            //Creating games with 4 players
            while (lobby.size() >= MAX_PLAYERS) {
                for (int j = 0; j < MAX_PLAYERS; j++) {
                    User u = lobby.get(j);
                    players.add(u);
                }
                game = new Game(players, additionalSchemas);
                printMessage("New match started with "+players.size()+" players");
                synchronized (lockGames) {
                    games.add(game);
                    lockGames.notifyAll();
                }
                game.start();
                lobby.removeAll(players);
                players=new ArrayList<>();
                lobbyChanged = true;

            }

            //Creating the last game with two or three players
            if (lobby.size() >= MIN_PLAYERS) {
                players.addAll(lobby);
                game = new Game(players, additionalSchemas);
                printMessage("New match started with "+players.size()+" players");
                synchronized (lockGames) {
                    games.add(game);
                    lockGames.notifyAll();
                }
                game.start();
                lobby.clear();
                lobbyChanged = true;
            }

            if (lobbyChanged && !lobby.isEmpty()) {
                lobby.get(0).getServerConn().notifyLobbyUpdate(lobby.size());
            }
        }
    }

    /**
     * this starts the master server with preferences loaded from the xml file
     * @throws InstantiationException
     */
    private static void startMasterServer() throws InstantiationException {
        instance= parser();
        if(instance==null){
            throw new InstantiationException();
        }
    }
    /**
     * Queues the users (logged and connected) or reconnect them in a match if they have previously lost the connection
     * @param user the user to check
     */
    public void updateConnected(User user){
        if(user.getStatus()==UserStatus.CONNECTED) {
            if (hasGameToReconnect(user)) {
                getGameByUser(user).reconnectUser(user);
            } else {
                synchronized (this.lobby) {
                    lobby.add(user);
                    user.setStatus(UserStatus.LOBBY);
                    ArrayList<User> tempLobby=new ArrayList<>();
                    tempLobby.addAll(lobby);
                    for (User l : tempLobby) {
                        l.getServerConn().notifyLobbyUpdate(tempLobby.size());
                    }
                    if (lobby.size() == MIN_PLAYERS) {
                        printMessage(TIMER_STARTING + lobbyTime + SEC);
                        Timer timer = new Timer();
                        timer.schedule(new LobbyHandler(), (long)lobbyTime * 1000);
                    }
                    if (lobby.size() >= MAX_PLAYERS) {
                        this.updateLobby();
                    }
                }
            }

            //PING ENABLE
            user.getServerConn().ping();
        }
    }

    /**
     * Checks if the user is disconnected and sends a message to the other users
     * @param user the user to check
     */
    void updateDisconnected(User user){
        synchronized (this.lobby){
            if(this.lobby.contains(user)) {
                lobby.remove(user);
                user.setStatus(UserStatus.DISCONNECTED);
            }
        }
        for(User lobbyingUser : lobby) {
            lobbyingUser.getServerConn().notifyLobbyUpdate(lobby.size());
        }

    }

    /**
     * This method makes the MasterServer available for rmi connection. It publishes in the rmi registry
     * an instance of the object Authenticator
     */
    private void startRMI(){
        printMessage(SERVER_STARTING_CONNECTIONS_VIA_RMI);
        System.setProperty(JAVA_RMI_SERVER_HOSTNAME,ipAddress);
        try {
            AuthenticationInt authenticator = new RMIAuthenticator();
            LocateRegistry.createRegistry(portRMI);
            Naming.rebind(RMI_SLASHSLASH +ipAddress+":"+portRMI+ AUTH, authenticator);
        }catch (RemoteException | MalformedURLException e){
            printMessage(ERR_STARTING_RMI);
            System.exit(0);
        }
    }


    /**
     * This method starts a SocketListener thread that accepts all socket connection
     */
    private void startSocket (){
        // server infinite loop
        new Thread(() -> {
            printMessage(SERVER_STARTING_CONNECTIONS_VIA_SOCKET);
            while(true) {
                Socket socket = null;
                try {
                    try( ServerSocket serverSocket = new ServerSocket(portSocket)) {
                        socket = serverSocket.accept();
                    }
                    printMessage(NEW_CONNECTION_ESTABLISHED_VIA_SOCKET);
                    SocketAuthenticator authenticator = new SocketAuthenticator(socket);
                    authenticator.start();
                } catch (IOException e) {
                    printMessage(ERR_STARTING_SOCKET);
                    System.exit(0);
                }

            }
        }).start();
    }

    /**
     * prints a message to the server's CLI
     * @param message the message to be printed
     */
    public static void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * This method allows a client to login to the MasterServer
     * @param username the username of the client who wants to login
     * @param password the password to be coupled with the username
     * @return true iff the Client gets logged in
     */
    public boolean login(String username, char[] password) {
        User user;
        synchronized (this.users) {
            if (this.isIn(username)) {
                user = getUser(username);
                if (Arrays.equals(password, user.getPassword()) && (user.getStatus() == UserStatus.DISCONNECTED)) {
                    user.setStatus(UserStatus.CONNECTED);
                    printMessage(LOGGED +username);
                    return true;
                }
                if(!Arrays.equals(password, user.getPassword())){
                    printMessage(WRONG_PASS +username);
                }else{
                    printMessage(USER_ALREADY_LOGGED_IN +username);
                }
            } else {
                user = new User(username, password);
                users.add(user);
                printMessage(LOGGED+username);
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks if the user was previously connected to a game and if this is still being played
     * @param user the user we want to check this about
     * @return true iff the user was previously connected to a game and if this is still being played
     */
    private boolean hasGameToReconnect(User user) {
        for(Game game: this.games){
            for(User u: game.getUsers()){
                if(u.equals(user) && game.canUserReconnect(u)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This methods returns the game the user needs to reconnect to if it has one
     * @param user the user who wants to reconnect
     * @return the game he can reconnect to iff present
     */
    private Game getGameByUser(User user) {
        assert hasGameToReconnect(user);
        for(Game game: this.games){
            for(User u: game.getUsers()){
                if(u.equals(user) && game.canUserReconnect(u)){
                    return game;
                }
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * this removes the ended game from the list of games active on the server
     * @param game the game to be deleted
     */
    void endGame(Game game){
        synchronized (lockGames) {
            games.remove(game);
            lockGames.notifyAll();
        }
    }


    /**
     * Metod to return the user whose name matches to the input String
     * @param username the name of the user searched
     * @return the searched user if present else null
     */
    public User getUser(String username){
        for(User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * This method checks if the user is already in the list of registered users
     * @param user the user to fing
     * @return true if present
     */
    private boolean isIn(String user){
        for(User u : users){
            if(u.getUsername().equals(user))
                return true;
        }
        return false;
    }

    /**
     * @return the ip address of the server
     */
    public String getIpAddress(){
        return this.ipAddress;
    }

    public static void main(String[] args){
        ArrayList<String> options=new ArrayList<>();
        try {
            MasterServer.startMasterServer();

            MasterServer server = MasterServer.getMasterServer();
            if (args.length > 0) {
                if (!ServerOptions.getOptions(args, options) || options.contains("h")) {
                    ServerOptions.printHelpMessage();
                    return;
                } else {
                    ServerOptions.setServerPreferences(options, server);
                }
            }
            if(server!=null) {
                server.startRMI();
                server.startSocket();
            }
        } catch (InstantiationException e) {
            logger.log(Level.INFO,e.getMessage());
            logger.log(Level.INFO,ERR_START_MASTER_SERVER);

        }
    }


}
