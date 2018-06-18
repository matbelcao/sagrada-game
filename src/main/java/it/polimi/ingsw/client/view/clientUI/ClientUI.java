package it.polimi.ingsw.client.view.clientUI;

import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.immutables.LightPrivObj;
import it.polimi.ingsw.common.immutables.LightSchemaCard;

import java.io.File;
import java.util.List;
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


    void showLastScreen();
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
     * this notifies the client that wanted to quit that his connection has been closed and he has successfully quit
     */
    void updateConnectionClosed();

    /**
     * this notifies an error in the connection towards the server
     */
    void updateConnectionBroken();


    void showWaitingForGameStartScreen();


    QueuedReader getCommandQueue();
}
