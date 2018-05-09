package it.polimi.ingsw;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Timer;

public class MasterServer {
    private static MasterServer instance;
    private String address;
    private int portRMI;
    private int portSocket;
    private ArrayList <User> users;
    private Timer timer;

    private MasterServer() {
        address = "rmi://127.0.0.1/myabc";
        portRMI = 1099;
        portSocket = 3000;

    }

    public static MasterServer getMasterServer() {
        if (instance == null) instance = new MasterServer();
        return instance;
    }

    public void startRMI(){
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            AuthenticationInt authenticator = new Authentication();
            Registry registry = LocateRegistry.createRegistry(portRMI);
            Naming.rebind("rmi://127.0.0.1/myabc", authenticator);
        }catch (RemoteException e){
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean loginRMI(String userName, String password){
        return false;
    }


    public static void main(String[] args){
        MasterServer.getMasterServer();
    }


}
