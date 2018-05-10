package it.polimi.ingsw;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Timer;

/**
 * This class is the server, it handles the login of the clients and the beginning of matches
 */

public class MasterServer {
    private static MasterServer instance;
    private SocketListener listener;
    private String address;
    private int portRMI;
    private int portSocket;
    private ArrayList <User> users;
    private Timer timer;

    /**
     * This is the constructor of the server, it initializes the address of the port for socket and RMI connection,
     * it's made private as MasterServer is a Singleton
     */
    private MasterServer() {
        address = "rmi://127.0.0.1/myabc";
        portRMI = 1099;
        portSocket = 3000;
        users = new ArrayList<>();
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
    public void startSocket(){
        SocketListener listener = new SocketListener(portSocket);
        listener.start();
    }

    /**
     * This method allows a client to login to the MasterServer
     * @param username the username of the client who wants to login
     * @param password the password to be coupled with the username
     * @return true iff the Client gets logged in
     */
    public synchronized boolean login(String username, String password){
        if(isIn(username)){
            if(getUser(username).getPassword().equals(password)) {
                //get connection
                return true;
            }else
                return false;
            }
        users.add(new User(username, password));
        return true;
    }

    public User getUser(String name){
        for(User u : users) {
            if (u.getUsername().equals(name)) {
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

    boolean isIn(String user){
        for(User u : users){
            if(u.getUsername().equals(user))
                return true;
        }
        return false;
    }

    public static void main(String[] args){
        MasterServer.getMasterServer();
        MasterServer.getMasterServer().startRMI();
        while(true){
            //server infinite loop
        }
    }


}
