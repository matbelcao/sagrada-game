package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.common.immutables.*;

import java.rmi.RemoteException;
import java.util.List;

public interface ClientConn {
    /**
     * The client invokes this method and then waits for a response from the server. This is typically the first communication
     * exchanged between client and server. The server will reply accordingly the authentication procedure
     * @param username the username of the user trying to login
     * @param password the password of the user
     * @return true iff the user has been logged into the server
     */
    boolean login(String username,char[] password);


    /**
     * This method tells the server the end of the session
     */
    void quit();

    /**
     * This function can be invoked to notify the server in case the client wants to end his turn before the timer goes off.
     */
    void endTurn();

    /**
     * This function can be invoked to request the updated schema card or the complete schema card (in case of reconnection
     * or if it’s the beginning of the first round).The draft option makes the server send the four schema cards the user
     * has to choose from.
     * @return the list of four schema cards immutable objects
     */
    List<LightSchemaCard> getSchemaDraft();

    /**
     * This function can be invoked to request the updated schema card or the complete schema card (in case of reconnection
     * or if it’s the beginning of the first round) of a scecific user.
     * @param playerId the id of the player's desired schema card
     * @return one schema card immutable object
     */
    LightSchemaCard getSchema(int playerId);

    /**
     * This function can be invoked to request the private objective card parameters
     * @return one private objective card immutable object
     */
    LightPrivObj getPrivateObject();

    /**
     * This function can be invoked to request the three public objective cards parameters
     * @return a list of three public objective cards immutable objects
     */
    List<LightCard> getPublicObjects();

    /**
     * This function can be invoked to request the three toolcards parameters
     * @return a list of three toolcards immutable objects
     */
    List<LightTool> getTools();

    /**
     * This function can be invoked to request the dice in the draftpool
     * @return a list of immutable dice contained in the draftpool
     */
    List<LightDie> getDraftPool();

    /**
     * This function can be invoked to request the dice in the roundtrack
     * @return a list of immutable dice contained in the roundtrack
     */
    List<List<LightDie>> getRoundtrack();

    /**
     * The client invokes this function to request the list of players of the match
     * @return a list of immutable players that are playing the match
     */
    List<LightPlayer> getPlayers();

    /**
     * This function can be invoked to get the number of tokens remaining to the specified player.
     * @param playerId the id of the player (0 to 3)
     * @return the number of favor tokens of the specific player
     */
    int getFavorTokens(int playerId);

    /**
     * This function can be invoked to obtain an immutable and indexed list containing the information about the dice placed
     * in the schema card
     * @return and immutable and indexed list containing the dice
     */
    List<IndexedCellContent> getSchemaDiceList();

    /**
     * This function can be invoked to obtain an immutable and indexed list containing the information about the dice placed
     * in the roundtrack
     * @return an immutable and indexed list containing the dice
     */
    List<IndexedCellContent> getRoundTrackDiceList();

    /**
     * This function can be invoked to obtain an immutable and indexed list containing the information about the dice placed
     * in the draft pool
     * @return an immutable and indexed list containing the dice
     */
    List<IndexedCellContent> getDraftpoolDiceList();

    /**
     * This function can be invoked to the server to specify the possible placements in the user’s schema of a die that is
     * temporarily selected by the user.
     * @param index the index (starting from 0) of the die in a given list
     * @return an immutable and indexed list of possible placements
     */
    List<Integer> selectDie(int index);

    /**
     *  This function can be invoked to notify the server in order to make a possibly definitive choice. The server is
     *  still going to do his checks and will reply.
     * @param type the subject of the choice (die_placement,schema or tool)
     * @param index the index of the object in the list previously sent by the server
     * @return true if the procedure is successful
     */
    boolean choose(String type, int index);

    /**
     * This message is sent to the server when the client that received a list of possible placement for a die chooses
     * not to place that die
     */
    void discard();

    /**
     * This method provides the ping functionality for the client-side hearthBreath thread
     * @return false iff the connection has broken
     */
    boolean pong() throws RemoteException;

    //ONLY FOR DEBUG PURPOSES
    void sendDebugMessage(String message);
}
