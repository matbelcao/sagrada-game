package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.ConnectionMode;
import it.polimi.ingsw.server.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIAuthenticator extends UnicastRemoteObject implements AuthenticationInt {

    RMIAuthenticator() throws RemoteException {}

    @Override
    public boolean authenticate(String username, String password) {
        MasterServer master = MasterServer.getMasterServer();
        boolean logged = master.login(username,password);
        if(logged){
            User user = master.getUser(username);
            user.setConnectionMode(ConnectionMode.RMI);
            try {
                RMIConn RMIconnection = new RMIConn(user);
                LocateRegistry.getRegistry(master.getIpAddress(),1099) ;
                Naming.rebind("rmi://"+master.getIpAddress()+"/"+username+password, RMIconnection);
                master.printMessage("RMI service for client "+username+" published"); //delete
                user.setServerConn(RMIconnection);
            }catch (RemoteException | MalformedURLException e){
                e.printStackTrace();
                logged = false;
            }
            master.updateConnected(user);
        }
        return logged;
    }
}
