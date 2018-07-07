package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.common.connection.QueuedReader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.client.controller.ClientFSM.*;

/**
 * this class implements the thread that manages the commands coming from the uis to perform changes on the client
 * status and view
 */
public class UICommandController extends Thread {
    private static final String ERR_COULDN_T_READ_FROM_CONSOLE = "ERR: couldn't read from console";
    private final ClientFSM clientFSM;
    private final QueuedReader commandQueue;
    private static final String INDEX = "([0-9]|([1-9][0-9]))";
    private static final String VALID_OPTION = "(["+BACK+QUIT+DISCARD+END_TURN+NEW_GAME+"])";

    /**
     * the constructor sets the needed parameters for the controller to work properly
     * @param clientFSM the fsm of the client
     * @param commandQueue the queue of commands that will need to be managed
     */
    UICommandController(ClientFSM clientFSM, QueuedReader commandQueue){
        this.clientFSM = clientFSM;
        this.commandQueue=commandQueue;
    }

    /**
     * this is the task that the thread has to perform, it simply waits for a new command and then elaborate on that
     */
    @Override
    public void run() {
        String command;

        while (clientFSM.isAlive()) {

            try {
                commandQueue.waitForLine();
            } catch (IOException e) {
                Logger.getGlobal().log(Level.INFO, ERR_COULDN_T_READ_FROM_CONSOLE);
                System.exit(2);
            }

            command = commandQueue.readln().trim();
            commandQueue.pop();

            if (command.matches(INDEX)) {

                clientFSM.evolve(Integer.parseInt(command));

            } else if (command.matches(VALID_OPTION)) {

                clientFSM.evolve(command.charAt(0));

            } else {
                clientFSM.invalidInput();
            }

        }

    }


}
