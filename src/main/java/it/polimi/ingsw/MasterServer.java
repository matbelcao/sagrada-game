package it.polimi.ingsw;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is the server, it handles the login of the clients and the beginning of matches
 */
public class MasterServer{
    private static MasterServer instance;
    private SocketAcceptor acceptor;
    private String address;
    private int portRMI;
    private int portSocket;
    private ArrayList <User> users;
    private ArrayList <User> lobby;
    private ArrayList <Game> games;

    /**
     * This is the constructor of the server, it initializes the address of the port for socket and RMI connection,
     * it's made private as MasterServer is a Singleton
     */
    private MasterServer() {
        File xmlFile= new File("src"+File.separator+"xml"+File.separator+"ServerConf.xml");
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

        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
        users = new ArrayList<>();
        lobby = new ArrayList<>();
        games = new ArrayList<>();
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

        //Queuing users
        for(User u : users){
            if(u.getStatus()==UserStatus.CONNECTED){
                u.setStatus(UserStatus.QUEUED);
                lobby.add(u);
            }
        }

        //Creating the games with 4 players
        for (int i=0; i<(Math.floor(lobby.size()/4))*4;i++){
            for (int j=0;j<4;j++){
                User u = lobby.get(j);
                players.add(u);
            }
            games.add(new Game(players));
            lobby.removeAll(players);
            players.clear();
        }

        //Creating the last game with 2<=players<4
        if (lobby.size()>=2){
            games.add(new Game(lobby));
            lobby.clear();
        }

        return;
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
            AuthenticationInt authenticator = new Authenticator();
            Registry registry = LocateRegistry.createRegistry(portRMI);
            Naming.rebind("rmi://127.0.0.1/myabc", authenticator);
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
        ServerSocket serverSocket=null;
        try
        {
            serverSocket = new ServerSocket(portSocket);
            System.out.println("\nServer waiting for socket connection on port " +  serverSocket.getLocalPort());

            // server infinite loop
            while(1==1)
            {
                Socket socket = serverSocket.accept();
                System.out.println("Client connection estabilished");
                SocketAcceptor acceptor = new SocketAcceptor(socket);
                acceptor.start();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * This method allows a client to login to the MasterServer
     * @param username the username of the client who wants to login
     * @param password the password to be coupled with the username
     * @return true iff the Client gets logged in
     */
    public synchronized boolean login(String username, String password) {
            if (isIn(username)) {
                User user = getUser(username);
                if (password.equals(user.getPassword()) && user.getStatus() != UserStatus.CONNECTED && user.getStatus() != UserStatus.QUEUED && user.getStatus() != UserStatus.PLAYING) {
                    return true;
                }
            } else {
                User user = new User(username, password);
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
    ArrayList getUsers(){ return users; }

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
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new LobbyQueue(), 0, 30 * 1000);
        MasterServer.getMasterServer().startRMI();
        MasterServer.getMasterServer().startSocket();
    }
}
