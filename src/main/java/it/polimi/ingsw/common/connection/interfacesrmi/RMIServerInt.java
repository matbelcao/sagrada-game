package it.polimi.ingsw.common.connection.interfacesrmi;

import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.common.exceptions.IllegalActionException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface offers the server's set of remote methods to the client
 */
public interface RMIServerInt extends Remote {

    /**
     * This function can be invoked to request the updated schema card or the complete schema card (in case of reconnection
     * or if it’s the beginning of the first round).The draft option makes the server send the four schema cards the user
     * has to choose from.
     * @return the list of four schema cards immutable objects
     */
    List<LightSchemaCard> getSchemaDraft() throws RemoteException, IllegalActionException;

    /**
     * This function can be invoked to request the updated schema card or the complete schema card (in case of reconnection
     * or if it’s the beginning of the first round) of a scecific user.
     * @param playerId the id of the player's desired schema card
     * @return one schema card immutable object
     */
    LightSchemaCard getSchema(int playerId) throws RemoteException, IllegalActionException;

    /**
     * This function can be invoked to request the private objective card parameters
     * @return one private objective card immutable object
     */
    LightPrivObj getPrivateObject() throws RemoteException;

    /**
     * This function can be invoked to request the three public objective cards parameters
     * @return a list of three public objective cards immutable objects
     */
    List<LightCard> getPublicObjectives() throws RemoteException;

    /**
     * This function can be invoked to request the three toolcards parameters
     * @return a list of three toolcards immutable objects
     */
    List<LightTool> getTools() throws RemoteException;

    /**
     * This function can be invoked to request the dice in the draftpool
     * @return a list of immutable dice contained in the draftpool
     */
    List<LightDie> getDraftPool() throws RemoteException, IllegalActionException;

    /**
     * This function can be invoked to request the dice in the roundtrack
     * @return a list of immutable dice contained in the roundtrack
     */
    List<List<LightDie>> getRoundTrack() throws RemoteException, IllegalActionException;

    /**
     * The client invokes this function to request the list of players of the match
     * @return a list of immutable players that are playing the match
     */
    List<LightPlayer> getPlayers() throws RemoteException;

    /**
     * The client invokest this function to retireve the necessary information to
     * guarantee the correct reconnection during the game.
     * @return the match status
     */
    LightGameStatus getGameStatus() throws RemoteException;

    /**
     * This function can be invoked to get the number of tokens remaining to the specified player.
     * @param playerId the id of the player (0 to 3)
     * @return the number of favor tokens of the specific player
     */
    int getFavorTokens(int playerId) throws RemoteException;

    /**
     * This function can be invoked to obtain an immutable and indexed list containing the information about the dice placed
     * in the actual selected element (schema,draftpool,roundtrack....)
     * @return an immutable and indexed list containing the dice
     */
    List<IndexedCellContent> getDiceList() throws RemoteException, IllegalActionException;

    /**
     * This function can be invoked to select one die of a previolsly SELECT_DIE command and obtain
     * a list of to options to manipulate it
     * @param dieIndex the index of the die to select
     * @return and immutable and indexed list containing the dice
     */
    List<Actions> select(int dieIndex) throws RemoteException, IllegalActionException;

    /**
     * This function can be invoked by the client to request the list of possible placements of a die (that is
     * temporarily selected by the user) in his schema card
     * @return an immutable and indexed list of possible placements
     */
    List<Integer> getPlacementsList() throws RemoteException, IllegalActionException;

    /**
     * This function can be invoked to notify the server in order to make a possibly definitive choice. The server is
     * still going to do his checks and will reply.
     * @param optionIndex the index of the object in the list previously sent by the server
     * @return true if the procedure is successful
     */
    boolean choose(int optionIndex) throws RemoteException, IllegalActionException;

    /**
     *  This function can be invoked to notify the server the intenction to select a tool car. The server is
     *  still going to do his checks and will reply.
     * @param toolIndex the index of the toolcard the user wants to use
     * @return true iff the toolcard has been activated
     */
    boolean enableTool(int toolIndex) throws RemoteException, IllegalActionException;

    /**
     * This function is invoked by the client to know if the toolcard's execution flow is still active
     * @return true iff the toolcard is active
     */
    boolean toolCanContinue() throws RemoteException, IllegalActionException;

    /**
     * This function can be invoked to notify the server in case the client wants to end his turn before the timer goes off.
     */
    void endTurn() throws RemoteException, IllegalActionException;

    /**
     * This message is sent to the server when the client that received a list of possible placement for a die chooses
     * not to place that die
     */
    void discard() throws RemoteException, IllegalActionException;

    /**
     * This message is sent to the server when the client wants to stop using a toolcard before it ends
     */
    void back() throws RemoteException, IllegalActionException;

    /**
     * This method tells the server the end of the session
     */
    void quit() throws RemoteException;

    /**
     * This function is invoked in case the client wants to start a new match when the previously is just ended
     */
    void newMatch() throws RemoteException;

    /**
     * This method provides the ping functionality for checking if the connection is still active
     */
    boolean pong() throws RemoteException;
}