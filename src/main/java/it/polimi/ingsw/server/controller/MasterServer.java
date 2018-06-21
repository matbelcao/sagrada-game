package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.server.ServerOptions;
import it.polimi.ingsw.server.connection.AuthenticationInt;
//import it.polimi.ingsw.server.connection.Heartbeat;
import it.polimi.ingsw.server.connection.RMIAuthenticator;
import it.polimi.ingsw.server.connection.SocketAuthenticator;
import it.polimi.ingsw.server.model.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


/**
 * This class is the server, it handles the login of the clients and the beginning of matches
 */
public class MasterServer{
    private static MasterServer instance;
    private String ipAddress;
    private int portRMI;
    private int portSocket;
    private boolean additionalSchemas; //to be used for additional schemas FA
    public static final String XML_SOURCE = "src"+ File.separator+"xml"+File.separator+"server"+File.separator; //append class name + ".xml" to obtain complete path
    private int lobbyTime;
    private int turnTime;
    private final ArrayList <User> users;
    private final ArrayList <User> lobby;
    private final ArrayList <Game> games;
    public static final int MIN_PLAYERS=2;
    public static final int MAX_PLAYERS=4;

    /**
     * This is the constructor of the server, it initializes the address of the port for socket and RMI connection,
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
        printMessage("--> STARTING :  Master Server");
        new LobbyHandler();
    }

    private static MasterServer parser(){

        File xmlFile= new File(XML_SOURCE+"ServerConf.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element eElement = (Element)doc.getElementsByTagName("conf").item(0);
            String setipAddress=eElement.getElementsByTagName("address").item(0).getTextContent();
            int setportRMI=Integer.parseInt(eElement.getElementsByTagName("portRMI").item(0).getTextContent());
            int setportSocket=Integer.parseInt(eElement.getElementsByTagName("portSocket").item(0).getTextContent());
            int setlobbyTime=Integer.parseInt(eElement.getElementsByTagName("timeLobby").item(0).getTextContent());
            int setturnTime=Integer.parseInt(eElement.getElementsByTagName("timeGame").item(0).getTextContent());
            boolean setadditionalSchemas=Boolean.parseBoolean(eElement.getElementsByTagName("additionalSchemas").item(0).getTextContent());
            return new MasterServer(setipAddress,setportSocket,setportRMI,setadditionalSchemas,setlobbyTime,setturnTime);
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
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

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setAdditionalSchemas(boolean additionalSchemas) {
        this.additionalSchemas = additionalSchemas;
    }

    public void setLobbyTime(int lobbyTime) {
        this.lobbyTime = lobbyTime;
    }

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

    public int getTurnTime() {
        return turnTime;
    }

    private static class LobbyHandler extends TimerTask {
        @Override
        public void run(){
            getMasterServer().updateLobby();
        }
    }

    /**
     * Updates the lobby queue and instantiate the new Games
     */
    protected void updateLobby() {

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
                games.add(game);
                game.start();
                lobby.removeAll(players);
                players=new ArrayList<>();
                lobbyChanged = true;

            }

            //Creating the last game with two or three players
            if (lobby.size() >= MIN_PLAYERS) {
                players.addAll(lobby);
                game = new Game(players, additionalSchemas);
                games.add(game);
                game.start();
                lobby.clear();
                lobbyChanged = true;
            }

            if (lobbyChanged && !lobby.isEmpty()) {
                lobby.get(0).getServerConn().notifyLobbyUpdate(lobby.size());
            }
        }
    }

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
                    for (User l : lobby) {
                        l.getServerConn().notifyLobbyUpdate(lobby.size());
                    }
                    if (lobby.size() == MIN_PLAYERS) {
                        System.out.println("TIMER STARTING " + lobbyTime + " sec");
                        Timer timer = new Timer();
                        timer.schedule(new LobbyHandler(), lobbyTime * 1000);
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
    public void updateDisconnected(User user){
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
     * This method makes the MasterServer available for RMI connection. It publishes in the rmi registry
     * an instance of the object Authenticator
     */
    public void startRMI(){
        System.setProperty("java.rmi.server.hostname",ipAddress);
        try {
            AuthenticationInt authenticator = new RMIAuthenticator();
            Registry registry = LocateRegistry.createRegistry(portRMI);
            Naming.rebind("rmi://"+ipAddress+"/auth", authenticator);
            printMessage("--> SERVER WAITING CONNECTIONS VIA RMI");
        }catch (RemoteException | MalformedURLException e){
            e.printStackTrace();
        }
    }



    /**
     * This method starts a SocketListener thread that accepts all socket connection
     */
    private void startSocket(){
        // server infinite loop
        new Thread(() -> {
            printMessage("--> SERVER WAITING CONNECTIONS VIA SOCKET");
            while(1==1) {
                Socket socket = null;
                try {
                    try( ServerSocket serverSocket = new ServerSocket(portSocket)) {
                        socket = serverSocket.accept();
                    }
                    printMessage("New connection established via Socket!");
                    SocketAuthenticator authenticator = new SocketAuthenticator(socket);
                    authenticator.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * Starts the HeartBeat service to detect the broken connections
     */
    private void startHeartBeat(){
        //Heartbeat heartbeat = new Heartbeat();
        //heartbeat.start();
    }

    /**
     * prints a message to the server's CLI
     * @param message the message to be printed
     */
    public void printMessage(String message) {
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
                    this.printMessage("Logged : "+username);
                    return true;
                }
                if(!Arrays.equals(password, user.getPassword())){
                    this.printMessage("Wrong password : "+username);
                }else{
                    this.printMessage("User already logged in : "+username);
                }
            } else {
                user = new User(username, password);
                users.add(user);
                this.printMessage("Logged : "+username);
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
        return null;
    }

    public void endGame(Game game){
        games.remove(game);
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

    public User getUserByIndex(int i){ return users.get(i); }

    /**
     * This method checks if the user is already in the list of registered users
     * @param user the user to fing
     * @return true if present
     */
    boolean isIn(String user){
        for(User u : users){
            if(u.getUsername().equals(user))
                return true;
        }
        return false;
    }


    /**
     * @return the number of users registered to the server
     */
    public int getUsersSize(){
        return users.size();
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
        } catch (InstantiationException e) {
            System.out.println("\u001B[31m"+"ERR: couldn't start the Master Server"+"\u001B[0m");
            return;
        }
        MasterServer server= MasterServer.getMasterServer();
        if (args.length>0) {
            if(!ServerOptions.getOptions(args,options) || options.contains("h")){
                ServerOptions.printHelpMessage();
                return;
            }else {
                ServerOptions.setServerPreferences(options, server);
            }
        }
        server.startRMI();
        server.startSocket();
        //server.startHeartBeat();
    }


}
