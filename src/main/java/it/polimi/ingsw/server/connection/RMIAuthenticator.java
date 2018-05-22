package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.ConnectionMode;

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
                RMIServer RMIconnection = new RMIServer(user);
                LocateRegistry.getRegistry(master.getIpAddress(),MasterServer.getMasterServer().getRMIPort()) ;
                Naming.rebind("rmi://"+master.getIpAddress()+"/"+username+password, RMIconnection);
                master.printMessage("RMI service for client "+username+" published"); //delete
                user.setServerConn(RMIconnection);
            }catch (RemoteException | MalformedURLException e){
                e.printStackTrace();
                logged = false;
                user.quit();
            }
            //master.updateConnected(user);
        }
        return logged;
    }

    @Override
    public void updateConnected(String username){
        MasterServer master = MasterServer.getMasterServer();
        //aggingere controllo che user non sia playing
        master.updateConnected(master.getUser(username));
    }

}
