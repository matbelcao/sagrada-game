package it.polimi.ingsw;

public class User {
    private String username;
    private String password;
    private UserStatus status;

    User(String username,String password){
       this.username = username;
       this.password = password;
       this.status = UserStatus.CONNECTED;
   }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public void setStatus(UserStatus status) { this.status=status; }

    public  UserStatus getStatus() { return status; }
}
