package it.polimi.ingsw.client.uielements;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ClientFSMState;
import it.polimi.ingsw.common.connection.QueuedReader;
import it.polimi.ingsw.common.enums.Commands;

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
                int index = Integer.parseInt(command);
                switch (client.getTurnState()) {

                    case CHOOSE_SCHEMA:
                        if (client.getClientConn().choose(index)) {

                            client.getClientUI().showWaitingForGameStartScreen();
                        } else {
                            client.getClientUI().showLastScreen();
                        }
                        break;

                    case MAIN:
                        switch (index) {
                            case 0:
                                client.getBoard().setLastDiceList(client.getClientConn().getDiceList());

                                synchronized (client.getLockState()) {
                                    client.setTurnState(client.getTurnState().nextState(false, false, false, false));
                                    client.getLockState().notifyAll();
                                }
                                break;
                            case 1:
                                synchronized (client.getLockState()) {
                                    client.setTurnState( client.getTurnState().nextState(true, false, false, false));
                                    client.getLockState().notifyAll();
                                }
                                break;
                            default:
                                client.getClientUI().showLastScreen();
                        }
                        client.getClientUI().updateBoard(client.getBoard());
                        break;

                    case CHOOSE_TOOL:
                        if (index < client.getBoard().NUM_TOOLS && index >= 0) {
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
                        break;
                    case SELECT_DIE:
                        if (client.getBoard().getDiceList().size() > index && index >= 0) {
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
                                }
                            } else {
                                client.getClientUI().showOptions(client.getBoard().getOptionsList());
                            }
                        } else {
                            client.getClientUI().showLastScreen();
                        }
                        break;
                    case CHOOSE_PLACEMENT:
                        break;
                    case CHOOSE_OPTION:
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

                        break;

                    default:
                        client.getClientUI().showLastScreen();
                        break;
                }
            } else if (command.matches(SINGLE_CHAR)) {

                switch (command) {

                    case QUIT:
                        client.quit();
                        break;
                    case END_TURN:
                        client.getClientConn().endTurn();
                        break;
                    case BACK:
                        client.getClientConn().exit();
                        break;
                    case DISCARD:
                        if (client.getTurnState().equals(ClientFSMState.CHOOSE_PLACEMENT)) {
                            client.getClientConn().discard();
                        }
                        break;

                    default:
                        client.getClientUI().showLastScreen();
                        break;
                }

                synchronized (client.getLockState()) {
                    client.setTurnState(client.getTurnState().nextState(false, command.equals(BACK), command.equals(END_TURN), command.equals(DISCARD)));
                    client.getLockState().notifyAll();
                }

            } else {
                client.getClientUI().showLastScreen();
            }

        }

    }
}
