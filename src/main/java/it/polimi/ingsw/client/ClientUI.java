package it.polimi.ingsw.client;

import it.polimi.ingsw.common.immutables.LightDie;
import it.polimi.ingsw.common.immutables.LightSchemaCard;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ClientUI {
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
     * this method is called after a successful login to show information about the lobby
     */
    void showLobby();

    /**
     * this method sends to the client an update regarding the number of players connected and waiting to begin a match
     * @param numUsers
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
     * @param board the elements of the game
     */
    void showDraftedSchemas(LightBoard board);

    /**
     * this message updates the the view right after the choice of the schemas made by the players
     * @param schemas the selected schemas of the players
     */
    void updateChosenSchemas(Map<Integer,LightSchemaCard> schemas);

    /**
     * this method signals the beginning of a new round
     * @param numRound the number of the round that is about to begin
     */
    void updateGameRoundStart(int numRound);

    /**
     * this method tells the users that a new turn is starting, if the player id is the same of the player receiving
     * the message he will be shown the default screen, where he'll be able to choose a die from the draftpool or to make
     * a different move
     * @param playerId the number of the player that is going to play the next turn
     * @param isFirstTurn tells if the the turn is the first or the second in the round
     */
    void updateGameTurnStart(int playerId, boolean isFirstTurn);


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
}
