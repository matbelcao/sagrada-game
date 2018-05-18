package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.ConnectionMode;
import it.polimi.ingsw.server.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIAuthenticator extends UnicastRemoteObject implements AuthenticationInt {
    MasterServer master = MasterServer.getMasterServer();
    RMIAuthenticator() throws RemoteException {}

    @Override
    public boolean authenticate(String username, String password) {
        boolean logged = MasterServer.getMasterServer().login(username,password);
        if(logged){
            User user = master.getUser(username);
            user.setConnectionMode(ConnectionMode.RMI);
            try {
                RMIConn RMIconnection = new RMIConn();
                LocateRegistry.getRegistry(MasterServer.getMasterServer().getIpAddress(),1099) ;
                Naming.rebind("rmi://"+MasterServer.getMasterServer().getIpAddress()+"/"+username+password, RMIconnection);
                System.out.println(username+password+" published"); //delete
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
