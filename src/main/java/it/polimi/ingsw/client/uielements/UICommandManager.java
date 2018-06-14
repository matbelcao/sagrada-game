package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ClientFSMState;
import it.polimi.ingsw.client.LightBoard;
import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.LightDie;

import java.io.IOException;

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
            client.setTurnState(ClientFSMState.CHOOSE_SCHEMA);
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
                if (client.getTurnState().equals(ClientFSMState.CHOOSE_PLACEMENT)) {
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

            default:
                client.getClientUI().showLastScreen();
                break;
        }
    }



    private void chooseOptionAction(int index) {
        if (client.getClientConn().choose(index)) {
            synchronized (client.getLockState()) {
                client.setTurnState( client.getTurnState().nextState(
                        client.getBoard().getOptionsList().get(index).equals(Commands.PLACE_DIE),
                        false,
                        false,
                        false));
                client.getLockState().notifyAll();
            }
        } else {
            client.getClientUI().showLastScreen();
        }

    }

    private void selectDieAction(int index) {
        if (client.getBoard().getDiceList().size() > index && index >= 0) {
            client.getBoard().setLastSelectedDie((LightDie) client.getBoard().getDiceList().get(index).getContent());
            client.getBoard().setOptionsList(client.getClientConn().select(index));
            if (client.getBoard().getOptionsList().size() == 1) {

                client.getClientConn().choose(0);
                synchronized (client.getLockState()) {
                    client.setTurnState(client.getTurnState().nextState(false, false, false, false));
                    client.setTurnState(client.getTurnState().nextState(
                            client.getBoard().getOptionsList().get(0).equals(Commands.PLACE_DIE),
                            false,
                            false,
                            false));
                    client.getLockState().notifyAll();

                    if(client.getTurnState().equals(ClientFSMState.CHOOSE_PLACEMENT)){
                        client.getBoard().setPlacementsList(client.getClientConn().getPlacementsList());

                    }
                }
            } else {
                client.getClientUI().showOptions(client.getBoard().getOptionsList());
            }
            client.getClientUI().updateBoard(client.getBoard());
        } else {
            client.getClientUI().showLastScreen();
        }
    }

    private boolean placedDieFromOutside(){
        return !client.getBoard().getDiceList().get(0).getPlace().equals(Place.SCHEMA);
    }

    private void choosePlacementAction(int index) {
        if(client.getClientConn().choose(index)) {
            synchronized (client.getLockState()) {
                client.setTurnState(client.getTurnState().nextState( placedDieFromOutside(), false, false, false));
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
                    client.setTurnState(client.getTurnState().nextState(true, false, false, false));
                    client.getLockState().notifyAll();
                }
                client.getBoard().setLastDiceList(client.getClientConn().getDiceList());
            } else {
                synchronized (client.getLockState()) {
                    client.setTurnState( client.getTurnState().nextState(false, false, false, false));
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
                if(ClientFSMState.isPlacedDie()){
                    client.getClientUI().showLastScreen();
                    return;

                }
                client.getBoard().setLastDiceList(client.getClientConn().getDiceList());

                synchronized (client.getLockState()) {
                    client.setTurnState(client.getTurnState().nextState(false, false, false, false));
                    client.getLockState().notifyAll();
                }
                break;
            case 0:
                synchronized (client.getLockState()) {
                    client.setTurnState( client.getTurnState().nextState(true, false, false, false));
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

            client.getClientUI().showWaitingForGameStartScreen();
        } else {
            client.getClientUI().showLastScreen();
        }
    }
}
