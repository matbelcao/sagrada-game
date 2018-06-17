package it.polimi.ingsw.client;

import it.polimi.ingsw.common.connection.QueuedReader;

import java.io.IOException;

/**
 * this class implements the thread that manages the commands coming from the uis to perform changes on the client
 * status and view
 */
public class UICommandController extends Thread {
    private final ClientFSM clientFSM;
    private final QueuedReader commandQueue;
    private static final String INDEX = "([0-9]|([1-9][0-9]))";
    private static final String SINGLE_CHAR = "([qebd])";

    /**
     * this sets the needed parameters for the manager to work properly
     * @param
     */
    public UICommandController(ClientFSM clientFSM, QueuedReader commandQueue){
        this.clientFSM = clientFSM;
        this.commandQueue=commandQueue;
    }

    /**
     * this is the task that the thread has to perform, it simply waits for a new command and then elaborate on that
     */
    @Override
    public void run() {
        String command = "";

        while (clientFSM.isAlive()) {

            try {
                commandQueue.waitForLine();
            } catch (IOException e) {
                System.err.println("ERR: couldn't read from console");
                System.exit(2);
            }

            command = commandQueue.readln().trim();
            commandQueue.pop();

            if (command.matches(INDEX)) {

                clientFSM.evolve(Integer.parseInt(command));

            } else if (command.matches(SINGLE_CHAR)) {

                clientFSM.evolve(command.charAt(0));

            } else {
                clientFSM.invalidInput();
            }

        }

    }


}
