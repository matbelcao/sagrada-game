package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.immutables.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ClientConn {
    /**
     * This method tries to login by "sending" the user's credentials to the server who's then going to check whether the user can or can not login and will reply accordingly
     * @param username the username of the user trying to login
     * @param password the password of the user
     * @return true iff the login was successful
     */
    boolean login(String username,String password);


    /**
     * This method tells the server the end of the session
     */
    void quit();

    /**
     * This method asks the server for the private objective of the user
     */
    LightCard getPrivateObject();

    /**
     * This method asks the server for the public objectives of the match, the server will send all three of them following this request
     */
    List<LightCard> getPublicObjects();

    /**
     * This method asks the server for the tools of the match, the server will send all three of them following this request
     */
    List<LightCard> getTools();

    /**
     * This method asks the serverfor an updated version of the draftpool
     */
    List<CellContent> getDraftPool();

    /**
     * This method asks the serverfor an updated version of the roundtrack
     */
    List<List<CellContent>> getRoundtrack();

    /**
     * this method queries the server for a list of the users that are playing the match that the user making the request is playing
     */
    List<LightPlayer> getPlayers();

    /**
     * this method asks the server for the remaining favor tokens of a player given his id
     * @param playerId the id of the player (0 to 3)
     */
    int getFavorTokens(int playerId);

    /**
     * this method gets an updated version of the schema of a player given his playerId
     * @param playerId the id of the player
     */
    LightSchemaCard getSchema(int playerId);

    /**
     * This method asks the server to draft four schemas for the initial choice of the player's schema
     */
    List<LightSchemaCard> getSchemaDraft();

    List<Integer> selectDie(int index);

    boolean selectTool(LightTool lightTool, int index);

    /**
     * This method is used to check the state of the connection of the user associated with the ClientConn
     * @return
     */
    boolean pong() throws RemoteException;

    //ONLY FOR DEBUG PURPOSES
    public void sendDebugMessage(String message);
}
