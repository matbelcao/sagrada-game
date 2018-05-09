package it.polimi.ingsw;

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

    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
