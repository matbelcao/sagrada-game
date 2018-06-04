package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.CommandQueue;
import it.polimi.ingsw.common.immutables.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Observer;

public interface ClientUI extends Observer {
    String MESSAGES_FILE="src"+ File.separator+"xml"+File.separator+"client"+File.separator+"UIMessages.xml";

    /**
     * this method prints a message that notifies the user that the connectioin to the server was correctly established
     */
    void updateConnectionOk();

    /**
     * this method asks via the ui to insert username and password and sets them in the client
     */
    void showLoginScreen();

    /**
     * this method notifies the user whether the login was successful or not
     * @param logged the outcome of the login (true iff it went fine)
     */
    void updateLogin(boolean logged);


    /**
     * this method sends to the client an update regarding the number of players connected and waiting to begin a match
     * @param numUsers the number of connected players at the moment
     */
    void updateLobby(int numUsers);

    /**
     * this method notifies the beginning of a new match
     * @param numUsers the number of participants
     * @param playerId the id of the user
     */
    void updateGameStart(int numUsers, int playerId);

    /**
     * this method is called right after the message that signals the start of a game and shows to the user elements
     * of the board and the drafted schemas to be able to make a choice of the schema based on them
     * @param draftedSchemas the schemas that have been drafted for this player
     * @param privObj the private objective of the player
     */
    void showDraftedSchemas(List<LightSchemaCard> draftedSchemas,LightPrivObj privObj);

    /**
     * this method updates the player's view with all other components of the board
     * @param board the newly created board for the game containing all the information needed to start a new match
     */
    void updateBoard(LightBoard board);

    /**
     * this updates the sole draftpool
     * @param draftpool the new draftpool
     */
    void updateDraftPool(List<LightDie> draftpool );

    /**
     * this updates the schema of a certain player identified by its id
     * @param player the player with its data and updated schema
     */
    void updateSchema(LightPlayer player);

    /**
     * this updates the roundtrack
     * @param roundtrack the updated roundtrack
     */
    void updateRoundTrack(List<List<LightDie>> roundtrack);

    void showRoundtrackDiceList(List<IndexedCellContent> roundtrack);

    void showDraftPoolDiceList(List<IndexedCellContent> draftpool);

    void showSchemaDiceList(List<IndexedCellContent> schema);

    void showTurnInitScreen();

    void showNotYourTurnScreen();

    //void showPossibleChangesToDie(List<ModifyDie> changes);
    // TODO: 31/05/2018  
    /**
     * this method tells the user that a new round has started / is about to start
     * @param numRound the number of the round that is about to begin
     * @param roundtrack the updated roundtrack
     */
    void updateRoundStart(int numRound,List<List<LightDie>> roundtrack);

    /**
     * this method tells the users that a new turn is starting, if the player id is the same of the player receiving
     * the message he will be shown the default screen, where he'll be able to choose a die from the draftpool or to make
     * a different move
     * @param playerId the number of the player that is going to play the next turn
     * @param isFirstTurn tells if the the turn is the first or the second in the round (usefool for a particular toolcard)
     * @param draftpool the updated draftpool
     */
    void updateTurnStart(int playerId, boolean isFirstTurn, Map<Integer,LightDie> draftpool);

    /**
     * this method updates the view of the user (broadcast) following the usage of a toolcard
     * @param tools the list of the three tools
     */
    void updateToolUsage(List<LightTool> tools);




    /**
     * this method is used
     */
    void updateStatusMessage(String statusChange, int playerId);

    /**
     * this notifies the client that wanted to quit that his connection has been closed and he has successfully quit
     */
    void updateConnectionClosed();

    /**
     * this notifies an error in the connection towards the server
     */
    void updateConnectionBroken();

    void printmsg(String msg);

    String getCommand();

    void setCommandQueue(CommandQueue commandQueue);
}
