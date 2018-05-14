package it.polimi.ingsw;

public class User {
    private String username;
    private String password;
    private UserStatus status;
    private ConnectionMode connectionMode;
    private ServerConn serverConn;

    User(String username,String password){
       this.username = username;
       this.password = password;
       this.status = UserStatus.CONNECTED;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public void setStatus(UserStatus status) { this.status=status; }

    public  UserStatus getStatus() { return status; }

    public void setConnectionMode(ConnectionMode connectionMode){
        this.connectionMode=connectionMode;
    }

    public ConnectionMode getConnectionMode() {
        return connectionMode;
    }

    public void setServerConn(ServerConn serverConn){
        this.serverConn=serverConn;
    }

    public ServerConn getServerConn() {
        return serverConn;
    }
}
