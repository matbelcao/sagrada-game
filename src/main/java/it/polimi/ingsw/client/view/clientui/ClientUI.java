package it.polimi.ingsw.client.view.clientui;

import it.polimi.ingsw.common.connection.QueuedReader;
import java.util.Observer;

public interface ClientUI extends Observer {

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


    void showLatestScreen();
    /**
     * this method sends to the client an update regarding the number of players connected and waiting to begin a match
     * @param numUsers the number of connected players at the moment
     */
    void updateLobby(int numUsers);


    /**
     * this notifies the client that wanted to quit that his connection has been closed and he has successfully quit
     */
    void updateConnectionClosed();

    /**
     * this notifies an error in the connection towards the server
     */
    void updateConnectionBroken();


    /**
     * this tells the user to wait for the new game
     */
    void showWaitingForGameStartScreen();

    /**
     * @return the command queue of the ui
     */
    QueuedReader getCommandQueue();
}
