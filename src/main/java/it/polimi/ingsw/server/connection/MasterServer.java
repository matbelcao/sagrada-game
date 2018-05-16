package it.polimi.ingsw.server.connection;
import it.polimi.ingsw.server.controller.Game;
import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.UserStatus;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is the server, it handles the login of the clients and the beginning of matches
 */
public class MasterServer{
    private static MasterServer instance;
    private String address;
    private int portRMI;
    private int portSocket;
    private boolean additionalSchemas; //to be used for additional schemas FA
    public static final String xmlSource = "src"+ File.separator+"xml"+File.separator; //append class name + ".xml" to obtain complete path
    private int timeLobby;
    private int timeGame;
    private ArrayList <User> users;
    private ArrayList <User> lobby;
    private ArrayList <Game> games;

    /**
     * This is the constructor of the server, it initializes the address of the port for socket and RMI connection,
     * it's made private as MasterServer is a Singleton
     */
    private MasterServer() {
        File xmlFile= new File(xmlSource+"ServerConf.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element eElement = (Element)doc.getElementsByTagName("conf").item(0);
            this.address=eElement.getElementsByTagName("address").item(0).getTextContent();
            this.portRMI=Integer.parseInt(eElement.getElementsByTagName("portRMI").item(0).getTextContent());
            this.portSocket=Integer.parseInt(eElement.getElementsByTagName("portSocket").item(0).getTextContent());
            this.timeLobby=Integer.parseInt(eElement.getElementsByTagName("timeLobby").item(0).getTextContent());
            this.timeGame=Integer.parseInt(eElement.getElementsByTagName("timeGame").item(0).getTextContent());
            this.additionalSchemas=Boolean.parseBoolean(eElement.getElementsByTagName("additionalSchemas").item(0).getTextContent());
        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
        users = new ArrayList<>();
        lobby = new ArrayList<>();
        games = new ArrayList<>();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new LobbyQueue(), 0, timeLobby* 1000);
    }

    /**
     * This is the getter of the MasterServer
     * @return the instance of the MasterServer
     */
    public static MasterServer getMasterServer() {
        if (instance == null) instance = new MasterServer();
        return instance;
    }

    /**
     * Provides timer to wakeup the updateLobby() method every X seconds
     */
    private static class LobbyQueue extends TimerTask {

        @Override
        public void run() {
            System.out.println("Updating Lobby");
            getMasterServer().updateLobby();
        }
    }

    /**
     * Updates the lobby queue and instantiate the new Games
     */
    protected synchronized void updateLobby(){
        ArrayList <User> players = new ArrayList<>();
        boolean lobbyChanged=false;

        //Creating the games with 4 players
        for (int i=0; i<(Math.floor(lobby.size()/4))*4;i++){
            for (int j=0;j<4;j++){
                User u = lobby.get(j);
                players.add(u);
            }
            games.add(new Game(players,additionalSchemas));
            lobby.removeAll(players);
            players.clear();
            lobbyChanged=true;
        }

        //Creating the last game with 2<=players<4
        if (lobby.size()>=2){
            games.add(new Game(lobby,additionalSchemas));
            lobby.clear();
            lobbyChanged=true;
        }

        //Sending the lobby message if there are new users or new games
        if(lobbyChanged){
            for(User l : lobby){
                l.getServerConn().lobbyUpdate(lobby.size());
            }
        }
        return;
    }

    /**
     * Queues the users (logged and connected) or reconnect them in a match if they have previously lost the connection
     * @param user the user to check
     */
    public synchronized void updateConnected(User user){
        boolean alreadyInGame=false;
        if(user.getStatus()==UserStatus.CONNECTED){
            for(Game g : games){
                if(g.getUsers().contains(user)){
                    alreadyInGame=true;
                    g.reconnectUser(user);
                }
            }
        }
        if(user.getStatus()==UserStatus.CONNECTED && !lobby.contains(user) && !alreadyInGame){
            user.setStatus(UserStatus.QUEUED);
            lobby.add(user);
            for(User l : lobby){
                l.getServerConn().lobbyUpdate(lobby.size());
            }
        }
    }

    /**
     * Checks if the user is disconnected and sends a message to the other users
     * @param user the user to check
     * @param previousStatus the previous status of the user
     */
    public synchronized void cleanDisconnected(User user,UserStatus previousStatus){
        if(user.getStatus()==UserStatus.DISCONNECTED && previousStatus==UserStatus.QUEUED){
            lobby.remove(user);
            for(User l : lobby){
                l.getServerConn().lobbyUpdate(lobby.size());
            }
        }
    }

    /**
     * Returns the number of players in the lobby
     * @return the lobby size
     */
    public int getLobbySize(){
        return lobby.size();
    }


    /**
     * This method makes the MasterServer available for RMI connection. It publishes in the rmi registry
     * an instance of the object Authenticator
     */
    public void startRMI(){
        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/

        try {
            AuthenticationInt authenticator = new RMIAuthenticator();
            Registry registry = LocateRegistry.createRegistry(portRMI);
            Naming.rebind(address, authenticator);
            System.out.println("rmi auth running");
        }catch (RemoteException e){
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }



    /**
     * This method starts a SocketListener thread that accepts all socket connection
     */
    private void startSocket(){
            // server infinite loop
        new Thread(() -> {
            MasterServer.getMasterServer().printMessage("server waiting for connections via socket");
            while(1==1) {
                Socket socket = null;
                try {
                    try( ServerSocket serverSocket = new ServerSocket(portSocket)) {
                        socket = serverSocket.accept();
                    }
                    MasterServer.getMasterServer().printMessage("connection established");
                    SocketAuthenticator authenticator = new SocketAuthenticator(socket);
                    authenticator.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    /**
     * prints a message to the server's CLI
     * @param s the message to be printed
     */
    private void printMessage(String s) {
        System.out.println(s);
    }

    /**
     * This method allows a client to login to the MasterServer
     * @param username the username of the client who wants to login
     * @param password the password to be coupled with the username
     * @return true iff the Client gets logged in
     */
    public synchronized boolean login(String username, String password) {
        User user;

        if (isIn(username)) {
            user = getUser(username);
            if (password.equals(user.getPassword()) && (user.getStatus() == UserStatus.DISCONNECTED )) {
                user.setStatus(UserStatus.CONNECTED);
                return true;
            }
        } else {
            user = new User(username, password);
            users.add(user);
            return true;
        }
        return false;
    }

    /**
     * Metod to return the user whose name matches to the input String
     * @param username the name of the user searched
     * @return the searched user if present else null
     */
    public User getUser(String username){
        for(User u : users) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * A method to get all the users registered to the MasterServer
     * @return users the arraylist of the users
     */
    public List getUsers(){ return users; }

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


    public static void main(String[] args){
        MasterServer.getMasterServer().startRMI();
        MasterServer.getMasterServer().startSocket();
    }
}
