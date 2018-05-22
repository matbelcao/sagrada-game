package it.polimi.ingsw.client.connection;

public interface ClientConn {
    /**
     * This method tries to login by "sending" the user's credentials to the server who's then going to check whether the user can or can not login and will reply accordingly
     * @param username the username of the user trying to login
     * @param password the password of the user
     * @return true iff the login was successful
     */
    public boolean login(String username,String password);

    /**
     * This method asks the server for the private objective of the user
     */
    public void getPrivateObj();

    /**
     * This method asks the server for the public objectives of the match, the server will send all three of them following this request
     */
    public void getPublicObj();

    /**
     * This method asks the server for the tools of the match, the server will send all three of them following this request
     */
    public void getTools();

    /**
     * This method asks the serverfor an updated version of the draftpool
     */
    public void getDraftPool();

    /**
     * This method asks the serverfor an updated version of the roundtrack
     */
    public void getRoundtrack();

    /**
     * this method queries the server for a list of the users that are playing the match that the user making the request is playing
     */
    public void getPlayers();

    /**
     * this method asks the server for the remaining favor tokens of a player given his id
     * @param playerId the id of the player (0 to 3)
     */
    public void getFavorTokens(int playerId);

    /**
     * this method gets an updated version of the schema of a player given his playerId
     * @param playerId the id of the player
     */
    public void getSchema(int playerId);

    /**
     * This method asks the server to draft four schemas for the initial choice of the player's schema
     */
    public void draftSchema();

    /**
     * This method is used to check the state of the connection of the user associated with the ClientConn
     * @return
     */
    public boolean ping();

    /**
     * This method is used in the initial phases of the login to gather the greeting message from the server
     * @return the message itself
     */
    String getGreetings();
    //...
}