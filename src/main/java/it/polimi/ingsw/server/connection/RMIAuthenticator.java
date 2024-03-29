package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.interfacesrmi.AuthenticationInt;
import it.polimi.ingsw.common.connection.interfacesrmi.RMIClientInt;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.controller.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the implementation of the RMI server-side methods used during the authentication
 */
public class RMIAuthenticator extends UnicastRemoteObject implements AuthenticationInt {

    private static final String RMI_SLASHSLASH = "rmi://";
    private static final String RMI_SERVICE_FOR_CLIENT = "rmi service for client ";
    private static final String PUBLISHED = " published";

    public RMIAuthenticator() throws RemoteException {
        //Not implemented
    }

    @Override
    public boolean authenticate(String username, char [] password) {
        MasterServer master = MasterServer.getMasterServer();
        boolean logged = master.login(username,password);
        if(logged){
            User user = master.getUser(username);
            try {
                LocateRegistry.getRegistry(master.getIpAddress(),MasterServer.getMasterServer().getRMIPort()) ;
                RMIServerObject serverObj=new RMIServerObject(user);
                Naming.rebind(RMI_SLASHSLASH +master.getIpAddress()+":"+master.getRMIPort()+"/"+username, serverObj);

                MasterServer.printMessage(RMI_SERVICE_FOR_CLIENT +username+ PUBLISHED); //delete
            }catch (RemoteException |MalformedURLException e){
                Logger.getGlobal().log(Level.INFO,e.getMessage());
                logged = false;
                user.quit();
            }

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
