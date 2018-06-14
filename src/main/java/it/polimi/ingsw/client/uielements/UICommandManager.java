package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.LightBoard;
import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.LightDie;

import java.io.IOException;

import static it.polimi.ingsw.client.ClientFSMState.*;

public class UICommandManager extends Thread {
    private static final String INDEX = "([0-9]|([1-9][0-9]))";
    private static final String SINGLE_CHAR = "([qebd])";
    private static final String QUIT = "q";
    private static final String END_TURN = "e";
    private static final String BACK = "b";
    private static final String DISCARD = "d";

    private final Client client;
    private final QueuedReader commandQueue;

    public UICommandManager(Client client){
        this.client = client;
        this.commandQueue=client.getClientUI().getCommandQueue();
    }

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

    private void manageOption(String command) {
        switch (command) {

            case QUIT:
                client.quit();
                break;
            case END_TURN:
                client.getClientConn().endTurn();
                break;
            case BACK:
                client.getClientConn().exit();
                client.getUpdates();
                break;
            case DISCARD:
                if (client.getTurnState().equals(CHOOSE_PLACEMENT)) {
                    client.getClientConn().discard();
                }
                break;

            default:
                client.getClientUI().showLastScreen();
                return;
        }

        synchronized (client.getLockState()) {
            client.setTurnState(client.getTurnState().nextState(false, command.equals(BACK), command.equals(END_TURN), command.equals(DISCARD)));
            client.getLockState().notifyAll();
        }
        client.getClientUI().updateBoard(client.getBoard());
    }

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
            case CHOOSE_PLACEMENT:
                choosePlacementAction(index);
                break;
            case CHOOSE_OPTION:
                chooseOptionAction(index);
                break;

            case TOOL_CAN_CONTINUE:
                break;
            default:
                client.getClientUI().showLastScreen();
                break;
        }
    }



    private void chooseOptionAction(int index) {
        client.getClientConn().choose(index);


        if (client.getBoard().getOptionsList().get(index).equals(Commands.PLACE_DIE)) {
            synchronized (client.getLockState()) {
                client.setTurnState(CHOOSE_OPTION.nextState(true));
                client.getLockState().notifyAll();
                client.getLockState().notifyAll();
            }

            client.getBoard().setPlacementsList(client.getClientConn().getPlacementsList());

        } else {
            synchronized (client.getLockState()) {
                client.setTurnState(CHOOSE_OPTION.nextState(false));
            }
            toolContinue();
        }
    }

    private void toolContinue() {

        boolean canContinue = client.getClientConn().toolCanContinue();
        synchronized (client.getLockState()) {
            client.setTurnState(TOOL_CAN_CONTINUE.nextState(canContinue));
            client.getLockState().notifyAll();
        }
        if(canContinue){
            client.getBoard().setLastDiceList(client.getClientConn().getDiceList());
        }
    }

    private void selectDieAction(int index) {
        if (client.getBoard().getDiceList().size() > index && index >= 0) {

            client.getBoard().setLastSelectedDie((LightDie) client.getBoard().getDiceList().get(index).getContent());
            client.getBoard().setOptionsList(client.getClientConn().select(index));

            if(client.getBoard().getOptionsList().isEmpty()){
                synchronized (client.getLockState()) {
                    client.setTurnState(SELECT_DIE.nextState(false,true,false,false));
                    client.getLockState().notifyAll();
                }

            } else if (client.getBoard().getOptionsList().size() == 1) {
                singleOption();
            } else {
                multipleOptions();
            }
            client.getClientUI().updateBoard(client.getBoard());
        } else {
            client.getClientUI().showLastScreen();
        }
    }

    private void multipleOptions() {

        synchronized (client.getLockState()) {
            client.setTurnState(SELECT_DIE.nextState(false));
            client.getLockState().notifyAll();
        }
        client.getClientUI().showOptions(client.getBoard().getOptionsList());
    }

    private void singleOption() {
        client.getClientConn().choose(0);
        synchronized (client.getLockState()) {
            client.setTurnState(SELECT_DIE.nextState(false));

            chooseOptionAction(0);
        }
    }



    private boolean isPlacedDieFromOutside(){
        return !client.getBoard().getDiceList().get(0).getPlace().equals(Place.SCHEMA);
    }

    private void choosePlacementAction(int index) {

        if(client.getClientConn().choose(index)) {

            synchronized (client.getLockState()) {
                client.setTurnState(CHOOSE_PLACEMENT.nextState( isPlacedDieFromOutside()));
                client.getLockState().notifyAll();
            }
            client.getUpdates();
            client.getClientUI().updateBoard(client.getBoard());
        }else{
            client.getClientUI().showLastScreen();
        }
    }



    private void chooseToolAction(int index) {
        if (index < LightBoard.NUM_TOOLS && index >= 0) {
            if (client.getClientConn().enableTool(index)) {
                synchronized (client.getLockState()) {
                    client.setTurnState(CHOOSE_TOOL.nextState(true));
                    client.getLockState().notifyAll();
                }
                client.getBoard().setLastDiceList(client.getClientConn().getDiceList());
            } else {
                synchronized (client.getLockState()) {
                    client.setTurnState( CHOOSE_TOOL.nextState(false));
                    client.getLockState().notifyAll();
                }
            }
            client.getClientUI().updateBoard(client.getBoard());
        } else {
            client.getClientUI().showLastScreen();
        }
    }

    private void mainChoiceAction(int index) {
        switch (index) {
            case 1:
                if(isPlacedDie()){
                    client.getClientUI().showLastScreen();
                    return;

                }
                client.getBoard().setLastDiceList(client.getClientConn().getDiceList());

                synchronized (client.getLockState()) {
                    client.setTurnState(MAIN.nextState(false));
                    client.getLockState().notifyAll();
                }
                break;
            case 0:
                synchronized (client.getLockState()) {
                    client.setTurnState( MAIN.nextState(true));
                    client.getLockState().notifyAll();
                }

                break;
            default:
                client.getClientUI().showLastScreen();
        }
        client.getClientUI().updateBoard(client.getBoard());
    }

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
}
