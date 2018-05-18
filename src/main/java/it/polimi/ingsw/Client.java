package it.polimi.ingsw;

import it.polimi.ingsw.server.connection.AuthenticationInt;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    private UIMode uiMode;
    private ConnectionMode connMode;
    private String username;
    private String password;

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

    public UIMode getUiMode() {
        return uiMode;
    }
    void loginRMI(){
        try {
            //authenticator = (Authentication)Naming.lookup("rmi://localhost:1099/auth");
            AuthenticationInt authenticator=(AuthenticationInt) Naming.lookup("rmi://127.0.0.1/myabc");
            authenticator.authenticate(username,password);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    void loginSocket(){
        /* TODO: 18/05/2018 implement method */
    }
}
