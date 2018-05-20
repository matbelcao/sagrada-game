package it.polimi.ingsw.client;

import it.polimi.ingsw.ConnectionMode;
import it.polimi.ingsw.UIMode;
import it.polimi.ingsw.server.connection.AuthenticationInt;
import it.polimi.ingsw.server.connection.RMIConnInt;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client {

    private UIMode uiMode;
    private ConnectionMode connMode;
    private String username;
    private String password;
    private ClientConn clientConn;

    public Client(UIMode uiMode,ConnectionMode connMode){
        this.uiMode = uiMode;
        this.connMode = connMode;
    }

    public Client(String uiMode,String connMode){
        this.uiMode = UIMode.valueOf(uiMode);
        this.connMode = ConnectionMode.valueOf(connMode);
    }

    public String getUsername() { return username; }

    public String getPassword(){ return password; }

    public void setUsername(String username){ this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public void setupConnection(){
        if(connMode.equals(ConnectionMode.RMI)){
            loginRMI();
        }else{
            loginSocket();
        }
    }

    public void setConnection(ClientConn clientConn){
        this.clientConn = clientConn;
    }
    public ClientConn getClientConn(){
        return clientConn;
    }

    public UIMode getUiMode() {
        return uiMode;
    }

    void loginRMI(){
        try {
            AuthenticationInt authenticator=(AuthenticationInt) Naming.lookup("rmi://127.0.0.1/auth");
            if(authenticator.authenticate(username,password)){
               //get the stub of the remote object
               RMIConnInt RMIConnStub = (RMIConnInt) Naming.lookup("rmi://127.0.0.1/"+username+password);
               //create RMIClient with the reference of the remote obj and assign it to the Client
               RMIClientInt rmiClient  = new RMIClient(RMIConnStub);
               clientConn = (RMIClient)rmiClient;
               //create a remote reference of the obj rmiClient and pass it to the server.
               //a remote reference is passed so there's no need to add rmiClient to a Registry
               RMIClientInt remoteRef = (RMIClientInt) UnicastRemoteObject.exportObject(rmiClient, 0);
               RMIConnStub.setClientReference(remoteRef);
            }
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    void loginSocket(){
        /* TODO: 18/05/2018 implement method */
    }

   /* public static void main(String[] args){
        Client c = new Client("CONSOLE","RMI");
        c.setUsername("c");
        c.setPassword("1");
        c.loginRMI();
        c.getClientConn().printToServer("message from client");
    }*/
}
