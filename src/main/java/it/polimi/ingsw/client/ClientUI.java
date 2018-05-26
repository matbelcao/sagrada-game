package it.polimi.ingsw.client;

import java.io.File;

public interface ClientUI {
    String MESSAGES_FILE="src"+ File.separator+"xml"+File.separator+"client"+File.separator+"UIMessages.xml";

    /**
     * this method asks via the ui to insert username and password and sets them in the client
     */
    void loginProcedure();

    /**
     * this method notifies the user whether the login was successful or not
     * @param logged the outcome of the login (true iff it went fine)
     */
    void updateLogin(boolean logged);

    /**
     * this method prints a message that notifies the user that the connectioin to the server was correctly established
     */
    void updateConnectionOk();

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
     * this method is used
     */
    void updateStatusMessage(String statusChange,int playerid);

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
