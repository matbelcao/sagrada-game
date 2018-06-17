package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.LightBoard;
import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;

import java.io.IOException;

import static it.polimi.ingsw.client.ClientFSMState.*;

/**
 * this class implements the thread that manages the commands coming from the uis to perform changes on the client
 * status and view
 */
public class UICommandManager extends Thread {
    private static final String INDEX = "([0-9]|([1-9][0-9]))";
    private static final String SINGLE_CHAR = "([qebd])";
    private static final String QUIT = "q";
    private static final String END_TURN = "e";
    private static final String BACK = "b";
    private static final String DISCARD = "d";

    private final Client client;
    private final QueuedReader commandQueue;

    /**
     * this sets the needed parameters for the manager to work properly
     * @param client the client object
     */
    public UICommandManager(Client client){
        this.client = client;
        this.commandQueue=client.getClientUI().getCommandQueue();
    }

    /**
     * this is the task that the thread has to perform, it simply waits for a new command and then elaborate on that
     */
    @Override
    public void run() {
        String command = "";
        synchronized (client.getLockState()) {
            client.setTurnState(CHOOSE_SCHEMA);
        }

        while (client.isLogged()) {

            try {
                commandQueue.waitForLine();
            } catch (IOException e) {
                System.err.println("ERR: couldn't read from console");
                System.exit(2);
            }

            command = commandQueue.readln();
            commandQueue.pop();

            if (command.matches(INDEX)) {

                manageIndex(command);

            } else if (command.matches(SINGLE_CHAR)) {

                manageOption(command);

            } else {
                client.getClientUI().showLastScreen();
            }

        }

    }

    /**
     * this method manages a command that is not an index but some letter, those letters have a particular meaning and can
     * trigger different actions
     * @param command the command received from the ui (SINGLE_CHAR)
     */
    private void manageOption(String command) {
        synchronized (client.getLockState()) {
            switch (command) {

                case QUIT:
                    client.quit();
                    break;
                case END_TURN:
                    if(!(client.getTurnState().equals(NOT_MY_TURN)||client.getTurnState().equals(CHOOSE_SCHEMA))) {
                        client.getClientConn().endTurn();
                    }else{
                        client.getClientUI().showLastScreen();
                    }
                    break;
                case BACK:
                    client.getClientConn().exit();
                    break;
                case DISCARD:
                    if (client.getTurnState().equals(CHOOSE_PLACEMENT)) {
                        client.getClientConn().discard();
                        client.getBoard().setLatestDiceList(client.getClientConn().getDiceList());
                    }else{
                        client.getClientUI().showLastScreen();
                    }
                    break;

                default:
                    client.getClientUI().showLastScreen();
                    return;
            }
            //state update

            client.setTurnState(client.getTurnState().nextState(false, command.equals(BACK), command.equals(END_TURN), command.equals(DISCARD)));
            client.getLockState().notifyAll();
        }
        client.getBoard().notifyObservers();
    }

    /**
     * this manages the indexes received from the ui that have a different meaning and trigger different procedures
     * according to the state of the client
     * @param command the index to be elaborated
     */
    private void manageIndex(String command) {
        int index = Integer.parseInt(command);
        switch (client.getTurnState()) {

            case CHOOSE_SCHEMA:
                chooseSchemaAction(index);
                break;

            case NOT_MY_TURN:
                break;

            case MAIN:
                mainChoiceAction(index);
                break;

            case CHOOSE_TOOL:
                chooseToolAction(index);
                break;

            case SELECT_DIE:
                selectDieAction(index);
                break;

            case CHOOSE_OPTION:
                chooseOptionAction(index);
                break;

            case CHOOSE_PLACEMENT:
                choosePlacementAction(index);
                break;


            case TOOL_CAN_CONTINUE:
                break;
            default:
                client.getClientUI().showLastScreen();
                break;
        }
    }

    /**
     * this allows the user to choose the schema to play with at the beginning of the match
     * @param index the index of the desired schema
     */
    private void chooseSchemaAction(int index) {
        if (client.getClientConn().choose(index)) {
            synchronized (client.getLockState()) {
                client.setTurnState( CHOOSE_SCHEMA.nextState(true));
                client.getLockState().notifyAll();
            }
            client.getClientUI().showWaitingForGameStartScreen();
        } else {
            client.getClientUI().showLastScreen();
        }
    }

    /**
     * this implements the choice of either placing a die or activating a tool
     * @param index the choice made by the player
     */
    private void mainChoiceAction(int index) {
        switch (index) {
            case 1:
                if(isPlacedDie()){
                    client.getClientUI().showLastScreen();
                    return;

                }
                client.getBoard().setLatestDiceList(client.getClientConn().getDiceList());
                synchronized (client.getLockState()) {
                    client.setTurnState(MAIN.nextState(false));
                    client.getLockState().notifyAll();
                }
                break;
            case 0:

                synchronized (client.getLockState()) {
                    client.setTurnState(MAIN.nextState(true));
                    client.getLockState().notifyAll();
                }
                client.getBoard().stateChanged();

                break;
            default:
                client.getClientUI().showLastScreen();
        }
        client.getBoard().notifyObservers();
    }


    /**
     * this implements the choice of a tool to be enabled
     * @param index the index of said tool
     */
    private void chooseToolAction(int index) {
        if (index < LightBoard.NUM_TOOLS && index >= 0) {
            if (client.getClientConn().enableTool(index)) {
                synchronized (client.getLockState()) {
                    client.setTurnState(CHOOSE_TOOL.nextState(true));
                    client.getLockState().notifyAll();
                }
                client.getBoard().setLatestDiceList(client.getClientConn().getDiceList());
                if(client.getBoard().getLatestDiceList().isEmpty()){
                    synchronized (client.getLockState()) {
                        client.setTurnState(SELECT_DIE.nextState(true));
                        client.getLockState().notifyAll();
                    }
                    toolContinue();
                }
            } else {
                synchronized (client.getLockState()) {
                    client.setTurnState( CHOOSE_TOOL.nextState(false));//back to MAIN
                    client.getLockState().notifyAll();
                }
                client.getBoard().stateChanged();
            }
            client.getBoard().notifyObservers();
        } else {
            client.getClientUI().showLastScreen();
        }
    }

    /**
     * this implements the actions needed after selecting a die (if the options received are one or less, this requires no
     * interaction with the user)
     * @param index the index of the selected die
     */
    private void selectDieAction(int index) {
        if (client.getBoard().getLatestDiceList().size() > index && index >= 0) {

            client.getBoard().setLatestSelectedDie( client.getBoard().getLatestDiceList().get(index));
            client.getBoard().setLatestOptionsList(client.getClientConn().select(index));

            if(client.getBoard().getLatestOptionsList().isEmpty()){
                synchronized (client.getLockState()) {
                    client.setTurnState(SELECT_DIE.nextState(true));
                    client.getLockState().notifyAll();
                }
                client.getBoard().stateChanged();

            } else if (client.getBoard().getLatestOptionsList().size() == 1) {
                singleOption();
            } else {
                multipleOptions();
            }
            client.getBoard().notifyObservers();
        } else {
            client.getClientUI().showLastScreen();
        }
    }

    /**
     * this is called in case the options received sum to one and simply makes an automatic choice for that option
     */
    private void singleOption() {
        synchronized (client.getLockState()) {
            client.setTurnState(SELECT_DIE.nextState(false));

            chooseOptionAction(0);
        }
    }

    /**
     * this is called in the rare case there are more than one options, it will display a list of possible choices to the
     * and set the state to CHOOSE_OPTION
     */
    private void multipleOptions() {

        synchronized (client.getLockState()) {
            client.setTurnState(SELECT_DIE.nextState(false));
            client.getLockState().notifyAll();
        }
        client.getClientUI().showOptions(client.getBoard().getLatestOptionsList());
    }


    /**
     * this manages a choice by index of an option
     * @param index the index of the chosen option
     */
    private void chooseOptionAction(int index) {
        if(client.getClientConn().choose(index)) {

            if (client.getBoard().getLatestOptionsList().get(index).equals(Commands.PLACE_DIE)) {
                synchronized (client.getLockState()) {
                    client.setTurnState(CHOOSE_OPTION.nextState(true));
                    client.getLockState().notifyAll();
                }

                client.getBoard().setLatestPlacementsList(client.getClientConn().getPlacementsList());
                if(client.getBoard().getLatestPlacementsList().isEmpty()){
                    if(isToolEnabled()) {
                        toolContinue();
                    }
                }
            } else {
                synchronized (client.getLockState()) {
                    client.setTurnState(CHOOSE_OPTION.nextState(false));
                }
                toolContinue();
            }
        }else {client.getClientUI().showLastScreen();}
    }

    /**
     * this method manages the TOOL_CAN_CONTINUE state without requiring interaction with the user
     */
    private void toolContinue() {

        boolean canContinue = client.getClientConn().toolCanContinue();
        synchronized (client.getLockState()) {
            client.setTurnState(TOOL_CAN_CONTINUE.nextState(canContinue));
            client.getLockState().notifyAll();
        }
        if(canContinue){
            client.getBoard().setLatestDiceList(client.getClientConn().getDiceList());
        }

        client.getBoard().notifyObservers();
    }



    /**
     * @return true iff the last dice list was from something other than the schema
     */
    private boolean isPlacedDieFromOutside(){
        return !client.getBoard().getLatestSelectedDie().getPlace().equals(Place.SCHEMA);
    }

    /**
     * this method implements the choice of a placement based on the passed index
     * @param index the chosen index
     */
    private void choosePlacementAction(int index) {

        if(client.getClientConn().choose(index)) {

            synchronized (client.getLockState()) {
                client.setTurnState(CHOOSE_PLACEMENT.nextState( isPlacedDieFromOutside()));
                client.getLockState().notifyAll();
            }
            if(client.getTurnState().equals(TOOL_CAN_CONTINUE)){
                toolContinue();
            }
            client.getBoard().notifyObservers();
        }else{
            client.getClientUI().showLastScreen();
        }
    }

}
