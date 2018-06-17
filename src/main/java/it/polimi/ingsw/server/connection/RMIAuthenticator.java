package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.connection.RMIClientInt;
import it.polimi.ingsw.client.connection.RMIClientObject;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.model.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIAuthenticator extends UnicastRemoteObject implements AuthenticationInt {

    public RMIAuthenticator() throws RemoteException {}

    @Override
    public boolean authenticate(String username, char [] password) {
        MasterServer master = MasterServer.getMasterServer();
        boolean logged = master.login(username,password);
        if(logged){
            User user = master.getUser(username);
            user.setConnectionMode(ConnectionMode.RMI);
            try {
                RMIClientInt RMIclientObj = new RMIClientObject(user);
                LocateRegistry.getRegistry(master.getIpAddress(),MasterServer.getMasterServer().getRMIPort()) ;
                Naming.rebind("rmi://"+master.getIpAddress()+"/"+username, RMIclientObj);
                master.printMessage("RMI service for client "+username+" published"); //delete
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
        master.updateConnected(master.getUser(username));
    }

    @Override
    public void setRemoteReference(RMIServerInt remoteRef, String username) {
        MasterServer master = MasterServer.getMasterServer();
        User user = master.getUser(username);
        user.setServerConn((new RMIServer(remoteRef, user)));
    }

}
