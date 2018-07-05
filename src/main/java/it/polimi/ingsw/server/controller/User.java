package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.connection.ServerConn;
import it.polimi.ingsw.server.controller.Game;

/**
 * This class contains the data concerning the user and the connection mode used
 */
public class User{
    private String username;
    private char [] password;
    private final Object lockStatus;
    private UserStatus status;
    private ConnectionMode connectionMode;
    private ServerConn serverConn;
    private Game game;

    /**
     * Instantiate the user profile and associates it's credentials
     * @param username the user's username
     * @param password the user's password
     */
    public User(String username, char[] password){
       this.username = username;
       this.password = password;
       this.status = UserStatus.CONNECTED;
       this.lockStatus=new Object();
    }

    /**
     * Return the user's username
     * @return the user's username
     */
    public String getUsername() { return username; }

    /**
     * Returns the user's password
     * @return the user password
     */
    public char[] getPassword() { return password; }

    public void disconnect(){
        UserStatus previousStatus=this.getStatus();
        if(previousStatus==UserStatus.LOBBY){
            MasterServer.getMasterServer().updateDisconnected(this);
        }
        if(previousStatus==UserStatus.PLAYING){
            this.getGame().disconnectUser(this);
        }
        MasterServer.getMasterServer().printMessage("Connection lost : "+this.getUsername());
    }

    public void quit(){
        UserStatus previousStatus=this.getStatus();
        if(previousStatus==UserStatus.LOBBY){
            MasterServer.getMasterServer().updateDisconnected(this);
            MasterServer.getMasterServer().printMessage("Quitted lobby : "+this.getUsername());
        }
        if(previousStatus==UserStatus.PLAYING){
            this.getGame().quitUser(this);
            MasterServer.getMasterServer().printMessage("Quitted match : "+this.getUsername());
        }
    }

    /**
     * Sets the user connection status (CONNECTED, PLAYING,....)
     * @param status the connection status to be set
     */
    public void setStatus(UserStatus status) {
        synchronized (lockStatus) {
            this.status = status;
            lockStatus.notifyAll();
        }
    }

    /**
     * Returns the user connection status (CONNECTED, PLAYING,....)
     * @return the user's connection status
     */
    public UserStatus getStatus() {
        synchronized (lockStatus) {
            return status;
        }
    }

    /**
     * Sets the user connection mode ( RMI_SLASHSLASH or SOCKET )
     * @param connectionMode the connection mode to be set
     */
    public void setConnectionMode(ConnectionMode connectionMode){
        this.connectionMode=connectionMode;
    }

    /**
     * Returns the user's connection mode ( RMI_SLASHSLASH or SOCKET )
     * @return the user's connection mode
     */
    public ConnectionMode getConnectionMode() {
        return connectionMode;
    }

    /**
     * Sets the user's connection class used for the communication
     * @param serverConn the user's connection
     */
    public void setServerConn(ServerConn serverConn){
        this.serverConn=serverConn;
    }

    /**
     * Returns the user's connection class used for the communication
     * @return the user's connection
     */
    public ServerConn getServerConn() {
        return serverConn;
    }

    /**
     * Sets the current match(game) that the user is playing
     * @param game the actual game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Return the current match(game) in which the player is involved
     * @return the player's game
     */
    public Game getGame() {
        return game;
    }


    public boolean isMyTurn(){
        if(game==null){return false;}
        if(!game.gameStarted()){
            return false;
        }
        return this.equals(game.getNowPlayingUser());
    }

    public void newMatch(){
        if(game.isGameEnded()){
            status=UserStatus.CONNECTED;
            MasterServer.getMasterServer().updateConnected(this);
            MasterServer.getMasterServer().printMessage("New match for: "+username);
        }
    }
    
}
