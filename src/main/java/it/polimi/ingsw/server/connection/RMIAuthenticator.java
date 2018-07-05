package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.rmi_interfaces.AuthenticationInt;
import it.polimi.ingsw.common.connection.rmi_interfaces.RMIClientInt;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.controller.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIAuthenticator extends UnicastRemoteObject implements AuthenticationInt {

    public static final String RMI_SLASHSLASH = "rmi://";
    public static final String RMI_SERVICE_FOR_CLIENT = "rmi service for client ";
    public static final String PUBLISHED = " published";

    public RMIAuthenticator() throws RemoteException {}

    @Override
    public boolean authenticate(String username, char [] password) {
        MasterServer master = MasterServer.getMasterServer();
        boolean logged = master.login(username,password);
        if(logged){
            User user = master.getUser(username);
            user.setConnectionMode(ConnectionMode.RMI);
            try {
                LocateRegistry.getRegistry(master.getIpAddress(),MasterServer.getMasterServer().getRMIPort()) ;
                RMIServerObject serverObj=new RMIServerObject(user);
                Naming.rebind(RMI_SLASHSLASH +master.getIpAddress()+":"+master.getRMIPort()+"/"+username, serverObj);

                master.printMessage(RMI_SERVICE_FOR_CLIENT +username+ PUBLISHED); //delete
            }catch (RemoteException |MalformedURLException e){
                e.printStackTrace();
                logged = false;
                user.quit();
            }
            //master.updateConnected(user); todo

        }
        return logged;
    }

    @Override
    public void updateConnected(String username){
        MasterServer master = MasterServer.getMasterServer();
        master.updateConnected(master.getUser(username));
    }

    @Override
    public void setRemoteReference(RMIClientInt remoteRef, String username) {
        MasterServer master = MasterServer.getMasterServer();
        User user = master.getUser(username);
        user.setServerConn((new RMIServer(remoteRef, user)));
    }

}
