package it.polimi.ingsw;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    private boolean usesGUI;
    private boolean usesRMI;
    private String username;
    private String password;

    Client(boolean usesGUI,boolean usesRMI){
        this.usesGUI = usesGUI;
        this.usesRMI = usesRMI;
    }

    public String getUsername() { return username; }

    public String getPassword(){ return password; }

    public void setUsername(String username){ this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public void setupConnection(){
        if(usesRMI){
            loginRMI();
        }else{
            loginSocket();
        }
    }

    void loginRMI(){
        try {
            //authenticator = (Authentication)Naming.lookup("rmi://localhost:1099/auth");
            AuthenticationInt autenticator=(AuthenticationInt) Naming.lookup("rmi://127.0.0.1/myabc");
            autenticator.authenticate(username,password);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void loginSocket(){

    }
}
