package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.connection.ServerConn;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;

/**
 * This class contains the data concerning the user and the connection mode used
 */
public class User{
    private String username;
    private char [] password;
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
    public synchronized void setStatus(UserStatus status) { this.status=status; }

    /**
     * Returns the user connection status (CONNECTED, PLAYING,....)
     * @return the user's connection status
     */
    public synchronized UserStatus getStatus() { return status; }

    /**
     * Sets the user connection mode ( RMI or SOCKET )
     * @param connectionMode the connection mode to be set
     */
    public synchronized void setConnectionMode(ConnectionMode connectionMode){
        this.connectionMode=connectionMode;
    }

    /**
     * Returns the user's connection mode ( RMI or SOCKET )
     * @return the user's connection mode
     */
    public synchronized ConnectionMode getConnectionMode() {
        return connectionMode;
    }

    /**
     * Sets the user's connection class used for the communication
     * @param serverConn the user's connection
     */
    public synchronized void setServerConn(ServerConn serverConn){
        this.serverConn=serverConn;
    }

    /**
     * Returns the user's connection class used for the communication
     * @return the user's connection
     */
    public synchronized ServerConn getServerConn() {
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
        return this.equals(game.getUserPlaying());
    }
    
}
